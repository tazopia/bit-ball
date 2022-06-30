package spoon.gameZone.eos2.service;

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
import spoon.gameZone.eos2.domain.Eos2Config;
import spoon.gameZone.eos2.domain.Eos2Dto;
import spoon.gameZone.eos2.domain.Eos2Score;
import spoon.gameZone.eos2.entity.Eos2;
import spoon.gameZone.eos2.entity.QEos2;
import spoon.gameZone.eos2.repository.Eos2Repository;
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
public class Eos2Service {

    private final ConfigService configService;

    private final MemberService memberService;

    private final Eos2GameService eos2GameService;

    private final Eos2BotService eos2BotService;

    private final Eos2Repository eos2Repository;

    private final BetItemRepository betItemRepository;

    private static final QEos2 q = QEos2.eos2;

    private final JPAQueryFactory queryFactory;

    @Transactional
    public boolean updateConfig(Eos2Config eos2Config) {
        try {
            configService.updateZoneConfig("zone_eos2", JsonUtils.toString(eos2Config));
            ZoneConfig.setEos2(eos2Config);
            // 이미 등록된 게임의 배당을 변경한다.
            eos2Repository.updateOdds(ZoneConfig.getEos2().getOdds());
        } catch (RuntimeException e) {
            log.error("EOS 2분 파워볼 설정 변경에 실패하였습니다. - {}", e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public Iterable<Eos2> getComplete() {
        return eos2Repository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    public Page<Eos2> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return eos2Repository.findAll(builder, pageable);
    }

    public Eos2Dto.Score findScore(Long id) {
        Eos2 eos2 = eos2Repository.findOne(id);

        Eos2Dto.Score score = new Eos2Dto.Score();
        score.setId(eos2.getId());
        score.setRound(eos2.getRound());
        score.setGameDate(eos2.getGameDate());
        score.setPb(eos2.getPb());
        score.setBall(eos2.getBall());
        score.setCancel(eos2.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos2Url() + "?sdate=" + eos2.getSdate());
        if (json == null) return score;

        eos2 = JsonUtils.toModel(json, Eos2.class);
        if (eos2 == null) return score;

        // 봇에 결과가 있다면
        if (eos2.isClosing()) {
            score.setPb(eos2.getPb());
            score.setBall(eos2.getBall());

            if (StringUtils.empty(eos2.getPb())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    public boolean closingGame(Eos2Dto.Score score) {
        Eos2 eos2 = eos2Repository.findOne(score.getId());

        try {
            if (eos2.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                eos2GameService.rollbackPayment(eos2);
            }

            // 스코어 입력
            eos2.updateScore(score);
            eos2Repository.saveAndFlush(eos2);
            eos2GameService.closingBetting(eos2);
            eos2BotService.checkResult();
        } catch (RuntimeException e) {
            log.error("파워볼 {} - {}회차 수동처리를 하지 못하였습니다. - {}", eos2.getGameDate(), eos2.getRound(), e.getMessage(), e);
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

        Iterable<Eos2> iterable = eos2Repository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(2))));
        for (Eos2 eos2 : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.EOS2).and(qi.groupId.eq(eos2.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos2Url() + "?sdate=" + eos2.getSdate());
            if (json == null) continue;

            Eos2 result = JsonUtils.toModel(json, Eos2.class);
            if (result == null) continue;

            if (result.isClosing()) {
                eos2.setOddeven(result.getOddeven());
                eos2.setPb_oddeven(result.getPb_oddeven());
                eos2.setOverunder(result.getOverunder());
                eos2.setPb_overunder(result.getPb_overunder());
                eos2.setSize(result.getSize());
                eos2.setSum(result.getSum());
                eos2.setPb(result.getPb());
                eos2.setBall(result.getBall());

                eos2.setClosing(true);
                eos2Repository.saveAndFlush(eos2);
                closing++;
            }
        }
        eos2BotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    public Eos2Dto.Config gameConfig() {
        Eos2Dto.Config gameConfig = new Eos2Dto.Config();
        Eos2Config config = ZoneConfig.getEos2();

        // 파워볼은 현재 회차에서 1을 더해준다.
        Date gameDate = config.getZoneMaker().getGameDate();
        Eos2 eos2 = eos2Repository.findOne(q.gameDate.eq(gameDate));

        if (eos2 == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isEos2());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(eos2.getGameDate());
        gameConfig.setSdate(eos2.getSdate());
        gameConfig.setRound(eos2.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(eos2.getOdds());

        int betTime = (int) (eos2.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
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

    public List<Eos2Score> getScore() {
        Iterable<Eos2> list = queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(5)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(Eos2Score::of).collect(Collectors.toList());
    }
}
