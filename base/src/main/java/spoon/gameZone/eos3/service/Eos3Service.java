package spoon.gameZone.eos3.service;

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
import spoon.gameZone.eos3.domain.Eos3Config;
import spoon.gameZone.eos3.domain.Eos3Dto;
import spoon.gameZone.eos3.domain.Eos3Score;
import spoon.gameZone.eos3.entity.Eos3;
import spoon.gameZone.eos3.entity.QEos3;
import spoon.gameZone.eos3.repository.Eos3Repository;
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
public class Eos3Service {

    private final ConfigService configService;

    private final MemberService memberService;

    private final Eos3GameService eos3GameService;

    private final Eos3BotService eos3BotService;

    private final Eos3Repository eos3Repository;

    private final BetItemRepository betItemRepository;

    private static final QEos3 q = QEos3.eos3;

    private final JPAQueryFactory queryFactory;

    @Transactional
    public boolean updateConfig(Eos3Config eos3Config) {
        try {
            configService.updateZoneConfig("zone_eos3", JsonUtils.toString(eos3Config));
            ZoneConfig.setEos3(eos3Config);
            // 이미 등록된 게임의 배당을 변경한다.
            eos3Repository.updateOdds(ZoneConfig.getEos3().getOdds());
        } catch (RuntimeException e) {
            log.error("EOS 3분 파워볼 설정 변경에 실패하였습니다. - {}", e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public Iterable<Eos3> getComplete() {
        return eos3Repository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    public Page<Eos3> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return eos3Repository.findAll(builder, pageable);
    }

    public Eos3Dto.Score findScore(Long id) {
        Eos3 eos3 = eos3Repository.findOne(id);

        Eos3Dto.Score score = new Eos3Dto.Score();
        score.setId(eos3.getId());
        score.setRound(eos3.getRound());
        score.setGameDate(eos3.getGameDate());
        score.setPb(eos3.getPb());
        score.setBall(eos3.getBall());
        score.setCancel(eos3.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos3Url() + "?sdate=" + eos3.getSdate());
        if (json == null) return score;

        eos3 = JsonUtils.toModel(json, Eos3.class);
        if (eos3 == null) return score;

        // 봇에 결과가 있다면
        if (eos3.isClosing()) {
            score.setPb(eos3.getPb());
            score.setBall(eos3.getBall());

            if (StringUtils.empty(eos3.getPb())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    public boolean closingGame(Eos3Dto.Score score) {
        Eos3 eos3 = eos3Repository.findOne(score.getId());

        try {
            if (eos3.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                eos3GameService.rollbackPayment(eos3);
            }

            // 스코어 입력
            eos3.updateScore(score);
            eos3Repository.saveAndFlush(eos3);
            eos3GameService.closingBetting(eos3);
            eos3BotService.checkResult();
        } catch (RuntimeException e) {
            log.error("파워볼 {} - {}회차 수동처리를 하지 못하였습니다. - {}", eos3.getGameDate(), eos3.getRound(), e.getMessage(), e);
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

        Iterable<Eos3> iterable = eos3Repository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(3))));
        for (Eos3 eos3 : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.EOS3).and(qi.groupId.eq(eos3.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos3Url() + "?sdate=" + eos3.getSdate());
            if (json == null) continue;

            Eos3 result = JsonUtils.toModel(json, Eos3.class);
            if (result == null) continue;

            if (result.isClosing()) {
                eos3.setOddeven(result.getOddeven());
                eos3.setPb_oddeven(result.getPb_oddeven());
                eos3.setOverunder(result.getOverunder());
                eos3.setPb_overunder(result.getPb_overunder());
                eos3.setSize(result.getSize());
                eos3.setSum(result.getSum());
                eos3.setPb(result.getPb());
                eos3.setBall(result.getBall());

                eos3.setClosing(true);
                eos3Repository.saveAndFlush(eos3);
                closing++;
            }
        }
        eos3BotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    public Eos3Dto.Config gameConfig() {
        Eos3Dto.Config gameConfig = new Eos3Dto.Config();
        Eos3Config config = ZoneConfig.getEos3();

        // 파워볼은 현재 회차에서 1을 더해준다.
        Date gameDate = config.getZoneMaker().getGameDate();
        Eos3 eos3 = eos3Repository.findOne(q.gameDate.eq(gameDate));

        if (eos3 == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isEos3());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(eos3.getGameDate());
        gameConfig.setSdate(eos3.getSdate());
        gameConfig.setRound(eos3.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(eos3.getOdds());

        int betTime = (int) (eos3.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
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

    public List<Eos3Score> getScore() {
        Iterable<Eos3> list = queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(5)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(Eos3Score::of).collect(Collectors.toList());
    }
}
