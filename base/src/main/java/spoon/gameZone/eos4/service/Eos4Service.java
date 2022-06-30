package spoon.gameZone.eos4.service;

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
import spoon.gameZone.eos4.domain.Eos4Config;
import spoon.gameZone.eos4.domain.Eos4Dto;
import spoon.gameZone.eos4.domain.Eos4Score;
import spoon.gameZone.eos4.entity.Eos4;
import spoon.gameZone.eos4.entity.QEos4;
import spoon.gameZone.eos4.repository.Eos4Repository;
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
public class Eos4Service {

    private final ConfigService configService;

    private final MemberService memberService;

    private final Eos4GameService eos4GameService;

    private final Eos4BotService eos4BotService;

    private final Eos4Repository eos4Repository;

    private final BetItemRepository betItemRepository;

    private static final QEos4 q = QEos4.eos4;

    private final JPAQueryFactory queryFactory;

    @Transactional
    public boolean updateConfig(Eos4Config eos4Config) {
        try {
            configService.updateZoneConfig("zone_eos4", JsonUtils.toString(eos4Config));
            ZoneConfig.setEos4(eos4Config);
            // 이미 등록된 게임의 배당을 변경한다.
            eos4Repository.updateOdds(ZoneConfig.getEos4().getOdds());
        } catch (RuntimeException e) {
            log.error("EOS 4분 파워볼 설정 변경에 실패하였습니다. - {}", e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public Iterable<Eos4> getComplete() {
        return eos4Repository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    public Page<Eos4> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return eos4Repository.findAll(builder, pageable);
    }

    public Eos4Dto.Score findScore(Long id) {
        Eos4 eos4 = eos4Repository.findOne(id);

        Eos4Dto.Score score = new Eos4Dto.Score();
        score.setId(eos4.getId());
        score.setRound(eos4.getRound());
        score.setGameDate(eos4.getGameDate());
        score.setPb(eos4.getPb());
        score.setBall(eos4.getBall());
        score.setCancel(eos4.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos4Url() + "?sdate=" + eos4.getSdate());
        if (json == null) return score;

        eos4 = JsonUtils.toModel(json, Eos4.class);
        if (eos4 == null) return score;

        // 봇에 결과가 있다면
        if (eos4.isClosing()) {
            score.setPb(eos4.getPb());
            score.setBall(eos4.getBall());

            if (StringUtils.empty(eos4.getPb())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    public boolean closingGame(Eos4Dto.Score score) {
        Eos4 eos4 = eos4Repository.findOne(score.getId());

        try {
            if (eos4.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                eos4GameService.rollbackPayment(eos4);
            }

            // 스코어 입력
            eos4.updateScore(score);
            eos4Repository.saveAndFlush(eos4);
            eos4GameService.closingBetting(eos4);
            eos4BotService.checkResult();
        } catch (RuntimeException e) {
            log.error("파워볼 {} - {}회차 수동처리를 하지 못하였습니다. - {}", eos4.getGameDate(), eos4.getRound(), e.getMessage(), e);
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

        Iterable<Eos4> iterable = eos4Repository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(4))));
        for (Eos4 eos4 : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.EOS4).and(qi.groupId.eq(eos4.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos4Url() + "?sdate=" + eos4.getSdate());
            if (json == null) continue;

            Eos4 result = JsonUtils.toModel(json, Eos4.class);
            if (result == null) continue;

            if (result.isClosing()) {
                eos4.setOddeven(result.getOddeven());
                eos4.setPb_oddeven(result.getPb_oddeven());
                eos4.setOverunder(result.getOverunder());
                eos4.setPb_overunder(result.getPb_overunder());
                eos4.setSize(result.getSize());
                eos4.setSum(result.getSum());
                eos4.setPb(result.getPb());
                eos4.setBall(result.getBall());

                eos4.setClosing(true);
                eos4Repository.saveAndFlush(eos4);
                closing++;
            }
        }
        eos4BotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    public Eos4Dto.Config gameConfig() {
        Eos4Dto.Config gameConfig = new Eos4Dto.Config();
        Eos4Config config = ZoneConfig.getEos4();

        // 파워볼은 현재 회차에서 1을 더해준다.
        Date gameDate = config.getZoneMaker().getGameDate();
        Eos4 eos4 = eos4Repository.findOne(q.gameDate.eq(gameDate));

        if (eos4 == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isEos4());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(eos4.getGameDate());
        gameConfig.setSdate(eos4.getSdate());
        gameConfig.setRound(eos4.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(eos4.getOdds());

        int betTime = (int) (eos4.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
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

    public List<Eos4Score> getScore() {
        Iterable<Eos4> list = queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(5)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(Eos4Score::of).collect(Collectors.toList());
    }
}
