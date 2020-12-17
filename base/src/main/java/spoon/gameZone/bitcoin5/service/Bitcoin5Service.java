package spoon.gameZone.bitcoin5.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.bet.entity.QBetItem;
import spoon.bet.repository.BetItemRepository;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.config.service.ConfigService;
import spoon.game.domain.MenuCode;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.bitcoin5.domain.Bitcoin5Config;
import spoon.gameZone.bitcoin5.domain.Bitcoin5Dto;
import spoon.gameZone.bitcoin5.domain.Bitcoin5Json;
import spoon.gameZone.bitcoin5.domain.Bitcoin5Score;
import spoon.gameZone.bitcoin5.entity.Bitcoin5;
import spoon.gameZone.bitcoin5.entity.QBitcoin5;
import spoon.gameZone.bitcoin5.repository.Bitcoin5Repository;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class Bitcoin5Service {

    private final MemberService memberService;

    private final ConfigService configService;

    private final Bitcoin5GameService bitcoin5GameService;

    private final Bitcoin5BotService bitcoin5BotService;

    private final Bitcoin5Repository bitcoin5Repository;

    private final BetItemRepository betItemRepository;

    private final JPAQueryFactory queryFactory;

    private static final QBitcoin5 q = QBitcoin5.bitcoin5;

    @Transactional
    public boolean updateConfig(Bitcoin5Config bitcoin5Config) {
        try {
            configService.updateZoneConfig("bitcoin5", JsonUtils.toString(bitcoin5Config));
            ZoneConfig.setBitcoin5(bitcoin5Config);
            // 이미 등록된 게임의 배당을 변경한다.
            bitcoin5Repository.updateOdds(ZoneConfig.getBitcoin5().getOdds());
        } catch (RuntimeException e) {
            log.error("비트코인 5분 설정 변경에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public Iterable<Bitcoin5> getComplete() {
        return bitcoin5Repository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    public Iterable<Bitcoin5> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return bitcoin5Repository.findAll(builder, pageable);
    }


    public Bitcoin5Dto.Score findScore(Long id) {
        Bitcoin5 entity = bitcoin5Repository.findOne(id);
        if (entity == null) return new Bitcoin5Dto.Score();

        Bitcoin5Dto.Score score = new Bitcoin5Dto.Score();
        score.setBs(entity.getBs());
        score.setCancel(entity.isCancel());
        score.setOpen(entity.getOpen());
        score.setClose(entity.getClose());
        score.setHigh(entity.getHigh());
        score.setLow(entity.getLow());
        score.setGameDate(entity.getGameDate());
        score.setRound(entity.getRound());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBitcoin5Url() + "?sdate=" + entity.getSdate());
        if (json == null) return score;

        Bitcoin5Json result = JsonUtils.toModel(json, Bitcoin5Json.class);
        if (result == null) return score;

        score.setBs(result.getBs());
        score.setOpen(result.getOpen());
        score.setClose(result.getClose());
        score.setHigh(result.getHigh());
        score.setLow(result.getLow());

        return score;
    }

    @Transactional
    public boolean closingGame(Bitcoin5Dto.Score score) {
        Bitcoin5 entity = bitcoin5Repository.getOne(score.getId());

        try {
            if (entity.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                bitcoin5GameService.rollbackPayment(entity);
            }

            // 스코어 입력
            entity.updateScore(score);
            bitcoin5Repository.saveAndFlush(entity);
            bitcoin5GameService.closingBetting(entity);
            bitcoin5BotService.checkResult();
        } catch (RuntimeException e) {
            log.error("비트코인 5분 {} - {}회차 수동처리를 하지 못하였습니다.", entity.getGameDate(), entity.getRound(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional
    public AjaxResult closingAllGame() {
        QBetItem qi = QBetItem.betItem;
        int total = 0;
        int closing = 0;

        Iterable<Bitcoin5> iterable = bitcoin5Repository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(5))));
        for (Bitcoin5 entity : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.BITCOIN5).and(qi.groupId.eq(entity.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBitcoin5Url() + "?sdate=" + entity.getSdate());
            if (json == null) continue;

            Bitcoin5Json result = JsonUtils.toModel(json, Bitcoin5Json.class);
            if (result == null) continue;

            try {
                entity.updateScore(result);
                bitcoin5GameService.closingBetting(entity);
                closing++;
            } catch (RuntimeException e) {
                log.error("비트코인 5분 {}회차 결과 업데이트에 실패하였습니다.", entity.getSdate(), e);
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            }
        }
        bitcoin5BotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    public Bitcoin5Dto.Config gameConfig() {
        Bitcoin5Dto.Config gameConfig = new Bitcoin5Dto.Config();
        Bitcoin5Config config = ZoneConfig.getBitcoin5();

        // 다음 회차를 가져간다.
        Date realDate = ZoneConfig.getBitcoin5().getZoneMaker().getGameDate();
        Date gameDate = new Date(realDate.getTime() + (60 * 1000 * 5));
        int round = Integer.parseInt(DateUtils.format(gameDate, "HHmm"), 10);
        Bitcoin5 entity = bitcoin5Repository.findOne(q.gameDate.eq(gameDate));

        if (entity == null) {
            gameConfig.setRound(round);
            gameConfig.setGameDate(realDate);
            gameConfig.setRound(Integer.parseInt(DateUtils.format(gameDate, "HHmm")));
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isBitcoin5());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(realDate);
        gameConfig.setSdate(entity.getSdate());
        gameConfig.setRound(entity.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(entity.getOdds());

        int betTime = (int) (entity.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime - (60 * 5));

        gameConfig.setHi_oe(config.isHi_oe());
        gameConfig.setHi_ou(config.isHi_ou());
        gameConfig.setHi_oe_ou(config.isHi_oe_ou());
        gameConfig.setLo_oe(config.isLo_oe());
        gameConfig.setLo_ou(config.isLo_ou());
        gameConfig.setLo_oe_ou(config.isLo_oe_ou());

        return gameConfig;
    }

    public Iterable<Bitcoin5Score> getScore() {
        Iterable<Bitcoin5> list = queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(5)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(Bitcoin5Score::of).collect(Collectors.toList());
    }
}
