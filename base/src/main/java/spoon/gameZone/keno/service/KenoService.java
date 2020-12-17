package spoon.gameZone.keno.service;

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
import spoon.gameZone.keno.domain.KenoConfig;
import spoon.gameZone.keno.domain.KenoDto;
import spoon.gameZone.keno.domain.KenoScore;
import spoon.gameZone.keno.entity.Keno;
import spoon.gameZone.keno.entity.QKeno;
import spoon.gameZone.keno.repository.KenoRepository;
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
public class KenoService {

    private final ConfigService configService;

    private final MemberService memberService;

    private final KenoGameService kenoGameService;

    private final KenoBotService kenoBotService;

    private final KenoRepository kenoRepository;

    private final BetItemRepository betItemRepository;

    private final JPAQueryFactory queryFactory;

    private static final QKeno q = QKeno.keno;

    @Transactional
    public boolean updateConfig(KenoConfig kenoConfig) {
        try {
            configService.updateZoneConfig("keno", JsonUtils.toString(kenoConfig));
            ZoneConfig.setKeno(kenoConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            kenoRepository.updateOdds(ZoneConfig.getKeno().getOdds());
        } catch (RuntimeException e) {
            log.error("키노사다리 설정 변경에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    public Iterable<Keno> getComplete() {
        return kenoRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    public Page<Keno> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return kenoRepository.findAll(builder, pageable);
    }

    public KenoDto.Score findScore(Long id) {
        Keno keno = kenoRepository.findOne(id);

        KenoDto.Score score = new KenoDto.Score();
        score.setSdate(keno.getSdate());
        score.setRound(keno.getRound());
        score.setOddeven(keno.getOddeven());
        score.setOverunder(keno.getOverunder());
        score.setCancel(keno.isCancel());
        score.setSum(keno.getSum());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getKenoUrl() + "?sdate=" + keno.getSdate());
        if (json == null) return score;

        keno = JsonUtils.toModel(json, Keno.class);
        if (keno == null) return score;

        // 봇에 결과가 있다면
        if (StringUtils.notEmpty(keno.getOddeven()) && StringUtils.notEmpty(keno.getOverunder())) {
            score.setOddeven(keno.getOddeven());
            score.setOverunder(keno.getOverunder());
            score.setSum(keno.getSum());

            if (!"ODD".equals(score.getOddeven()) && !"EVEN".equals(score.getOddeven())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    public boolean closingGame(KenoDto.Score score) {
        Keno keno = kenoRepository.findOne(score.getId());

        try {
            if (keno.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                kenoGameService.rollbackPayment(keno);
            }

            // 스코어 입력
            keno.updateScore(score);
            kenoRepository.saveAndFlush(keno);
            kenoGameService.closingBetting(keno);
            kenoBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("키노사다리 {} - {}회차 수동처리를 하지 못하였습니다.", keno.getGameDate(), keno.getRound(), e);
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

        Iterable<Keno> iterable = kenoRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(5))));
        for (Keno keno : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.KENO).and(qi.groupId.eq(keno.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getKenoUrl() + "?sdate=" + keno.getSdate());
            if (json == null) continue;

            Keno result = JsonUtils.toModel(json, Keno.class);
            if (result == null) continue;

            if (result.isClosing()) {
                keno.setSum(result.getSum());
                keno.setOverunder(result.getOverunder());
                keno.setOddeven(result.getOddeven());
                keno.setClosing(true);
                kenoRepository.saveAndFlush(keno);
                closing++;
            }
        }
        kenoBotService.checkResult();
        return new AjaxResult(true, "스피드키노 전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    public KenoDto.Config gameConfig() {
        KenoDto.Config gameConfig = new KenoDto.Config();
        KenoConfig config = ZoneConfig.getKeno();

        Date gameDate = config.getZoneMaker().getGameDate();
        Keno keno = kenoRepository.findOne(q.gameDate.eq(gameDate));

        if (keno == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isKeno());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(keno.getGameDate());
        gameConfig.setSdate(keno.getSdate());
        gameConfig.setRound(keno.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(keno.getOdds());

        int betTime = (int) (keno.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setOverunder(config.isOverunder());

        return gameConfig;
    }

    public Iterable<KenoScore> getScore() {
        Iterable<Keno> list = queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(6)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(KenoScore::of).collect(Collectors.toList());
    }
}
