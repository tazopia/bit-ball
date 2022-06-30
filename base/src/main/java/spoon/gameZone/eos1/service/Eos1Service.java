package spoon.gameZone.eos1.service;

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
import spoon.gameZone.eos1.domain.Eos1Config;
import spoon.gameZone.eos1.domain.Eos1Dto;
import spoon.gameZone.eos1.domain.Eos1Score;
import spoon.gameZone.eos1.entity.Eos1;
import spoon.gameZone.eos1.entity.QEos1;
import spoon.gameZone.eos1.repository.Eos1Repository;
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
public class Eos1Service {

    private final ConfigService configService;

    private final MemberService memberService;

    private final Eos1GameService eos1GameService;

    private final Eos1BotService eos1BotService;

    private final Eos1Repository eos1Repository;

    private final BetItemRepository betItemRepository;

    private static final QEos1 q = QEos1.eos1;

    private final JPAQueryFactory queryFactory;

    @Transactional
    public boolean updateConfig(Eos1Config eos1Config) {
        try {
            configService.updateZoneConfig("zone_eos1", JsonUtils.toString(eos1Config));
            ZoneConfig.setEos1(eos1Config);
            // 이미 등록된 게임의 배당을 변경한다.
            eos1Repository.updateOdds(ZoneConfig.getEos1().getOdds());
        } catch (RuntimeException e) {
            log.error("EOS 1분 파워볼 설정 변경에 실패하였습니다. - {}", e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public Iterable<Eos1> getComplete() {
        return eos1Repository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    public Page<Eos1> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return eos1Repository.findAll(builder, pageable);
    }

    public Eos1Dto.Score findScore(Long id) {
        Eos1 eos1 = eos1Repository.findOne(id);

        Eos1Dto.Score score = new Eos1Dto.Score();
        score.setId(eos1.getId());
        score.setRound(eos1.getRound());
        score.setGameDate(eos1.getGameDate());
        score.setPb(eos1.getPb());
        score.setBall(eos1.getBall());
        score.setCancel(eos1.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos1Url() + "?sdate=" + eos1.getSdate());
        if (json == null) return score;

        eos1 = JsonUtils.toModel(json, Eos1.class);
        if (eos1 == null) return score;

        // 봇에 결과가 있다면
        if (eos1.isClosing()) {
            score.setPb(eos1.getPb());
            score.setBall(eos1.getBall());

            if (StringUtils.empty(eos1.getPb())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    public boolean closingGame(Eos1Dto.Score score) {
        Eos1 eos1 = eos1Repository.findOne(score.getId());

        try {
            if (eos1.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                eos1GameService.rollbackPayment(eos1);
            }

            // 스코어 입력
            eos1.updateScore(score);
            eos1Repository.saveAndFlush(eos1);
            eos1GameService.closingBetting(eos1);
            eos1BotService.checkResult();
        } catch (RuntimeException e) {
            log.error("파워볼 {} - {}회차 수동처리를 하지 못하였습니다. - {}", eos1.getGameDate(), eos1.getRound(), e.getMessage(), e);
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

        Iterable<Eos1> iterable = eos1Repository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(1))));
        for (Eos1 eos1 : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.EOS1).and(qi.groupId.eq(eos1.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos1Url() + "?sdate=" + eos1.getSdate());
            if (json == null) continue;

            Eos1 result = JsonUtils.toModel(json, Eos1.class);
            if (result == null) continue;

            if (result.isClosing()) {
                eos1.setOddeven(result.getOddeven());
                eos1.setPb_oddeven(result.getPb_oddeven());
                eos1.setOverunder(result.getOverunder());
                eos1.setPb_overunder(result.getPb_overunder());
                eos1.setSize(result.getSize());
                eos1.setSum(result.getSum());
                eos1.setPb(result.getPb());
                eos1.setBall(result.getBall());

                eos1.setClosing(true);
                eos1Repository.saveAndFlush(eos1);
                closing++;
            }
        }
        eos1BotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    public Eos1Dto.Config gameConfig() {
        Eos1Dto.Config gameConfig = new Eos1Dto.Config();
        Eos1Config config = ZoneConfig.getEos1();

        // 파워볼은 현재 회차에서 1을 더해준다.
        Date gameDate = config.getZoneMaker().getGameDate();
        Eos1 eos1 = eos1Repository.findOne(q.gameDate.eq(gameDate));

        if (eos1 == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isEos1());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(eos1.getGameDate());
        gameConfig.setSdate(eos1.getSdate());
        gameConfig.setRound(eos1.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(eos1.getOdds());

        int betTime = (int) (eos1.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
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

    public List<Eos1Score> getScore() {
        Iterable<Eos1> list = queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(5)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(Eos1Score::of).collect(Collectors.toList());
    }
}
