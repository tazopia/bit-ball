package spoon.gameZone.eos5.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
import spoon.gameZone.eos5.domain.Eos5Config;
import spoon.gameZone.eos5.domain.Eos5Dto;
import spoon.gameZone.eos5.domain.Eos5Score;
import spoon.gameZone.eos5.entity.Eos5;
import spoon.gameZone.eos5.entity.QEos5;
import spoon.gameZone.eos5.repository.Eos5Repository;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos5Service {

    private final ConfigService configService;

    private final MemberService memberService;

    private final Eos5GameService eos5GameService;

    private final Eos5BotService eos5BotService;

    private final Eos5Repository eos5Repository;

    private final BetItemRepository betItemRepository;

    private static final QEos5 q = QEos5.eos5;

    private final JPAQueryFactory queryFactory;

    @Transactional
    public boolean updateConfig(Eos5Config eos5Config) {
        try {
            configService.updateZoneConfig("zone_eos5", JsonUtils.toString(eos5Config));
            ZoneConfig.setEos5(eos5Config);
            // 이미 등록된 게임의 배당을 변경한다.
            eos5Repository.updateOdds(ZoneConfig.getEos5().getOdds());
        } catch (RuntimeException e) {
            log.error("EOS 5분 파워볼 설정 변경에 실패하였습니다. - {}", e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public Iterable<Eos5> getComplete() {
        return eos5Repository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    public Page<Eos5> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return eos5Repository.findAll(builder, pageable);
    }

    public Eos5Dto.Score findScore(Long id) {
        Eos5 eos5 = eos5Repository.findOne(id);

        Eos5Dto.Score score = new Eos5Dto.Score();
        score.setId(eos5.getId());
        score.setRound(eos5.getRound());
        score.setGameDate(eos5.getGameDate());
        score.setPb(eos5.getPb());
        score.setBall(eos5.getBall());
        score.setCancel(eos5.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos5Url() + "?sdate=" + eos5.getSdate());
        if (json == null) return score;

        eos5 = JsonUtils.toModel(json, Eos5.class);
        if (eos5 == null) return score;

        // 봇에 결과가 있다면
        if (eos5.isClosing()) {
            score.setPb(eos5.getPb());
            score.setBall(eos5.getBall());

            if (StringUtils.empty(eos5.getPb())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    public boolean closingGame(Eos5Dto.Score score) {
        Eos5 eos5 = eos5Repository.findOne(score.getId());

        try {
            if (eos5.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                eos5GameService.rollbackPayment(eos5);
            }

            // 스코어 입력
            eos5.updateScore(score);
            eos5Repository.saveAndFlush(eos5);
            eos5GameService.closingBetting(eos5);
            eos5BotService.checkResult();
        } catch (RuntimeException e) {
            log.error("파워볼 {} - {}회차 수동처리를 하지 못하였습니다. - {}", eos5.getGameDate(), eos5.getRound(), e.getMessage(), e);
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

        Iterable<Eos5> iterable = eos5Repository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(1))));
        for (Eos5 eos5 : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.EOS5).and(qi.groupId.eq(eos5.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos5Url() + "?sdate=" + eos5.getSdate());
            if (json == null) continue;

            Eos5 result = JsonUtils.toModel(json, Eos5.class);
            if (result == null) continue;

            if (result.isClosing()) {
                eos5.setOddeven(result.getOddeven());
                eos5.setPb_oddeven(result.getPb_oddeven());
                eos5.setOverunder(result.getOverunder());
                eos5.setPb_overunder(result.getPb_overunder());
                eos5.setSize(result.getSize());
                eos5.setSum(result.getSum());
                eos5.setPb(result.getPb());
                eos5.setBall(result.getBall());

                eos5.setClosing(true);
                eos5Repository.saveAndFlush(eos5);
                closing++;
            }
        }
        eos5BotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    public Eos5Dto.Config gameConfig() {
        Eos5Dto.Config gameConfig = new Eos5Dto.Config();
        Eos5Config config = ZoneConfig.getEos5();

        // 파워볼은 현재 회차에서 1을 더해준다.
        Date gameDate = config.getZoneMaker().getGameDate();
        Eos5 eos5 = eos5Repository.findOne(q.gameDate.eq(gameDate));

        if (eos5 == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isEos5());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(eos5.getGameDate());
        gameConfig.setSdate(eos5.getSdate());
        gameConfig.setRound(eos5.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(eos5.getOdds());

        int betTime = (int) (eos5.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setPb_oddeven(config.isPb_oddeven());
        gameConfig.setOverunder(config.isOverunder());
        gameConfig.setPb_overunder(config.isPb_overunder());
        gameConfig.setSize(config.isSize());
        gameConfig.setOe_ou(config.isOe_ou());
        gameConfig.setOe_size(config.isOe_size());
        gameConfig.setPb_oe_ou(config.isPb_oe_ou());

        return gameConfig;
    }

    public List<Eos5Score> getScore() {
        Iterable<Eos5> list = queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(5)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(Eos5Score::of).collect(Collectors.toList());
    }
}
