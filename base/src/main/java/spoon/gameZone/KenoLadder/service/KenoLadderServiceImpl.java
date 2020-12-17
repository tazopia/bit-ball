package spoon.gameZone.KenoLadder.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
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
import spoon.common.utils.*;
import spoon.config.domain.Config;
import spoon.config.service.ConfigService;
import spoon.game.domain.MenuCode;
import spoon.gameZone.KenoLadder.*;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneDto;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Service
public class KenoLadderServiceImpl implements KenoLadderService {

    private ConfigService configService;

    private MemberService memberService;

    private KenoLadderGameService kenoLadderGameService;

    private KenoLadderBotService kenoLadderBotService;

    private KenoLadderRepository kenoLadderRepository;

    private BetItemRepository betItemRepository;

    private JPAQueryFactory queryFactory;

    private static QKenoLadder q = QKenoLadder.kenoLadder;

    @Transactional
    @Override
    public boolean updateConfig(KenoLadderConfig kenoLadderConfig) {
        try {
            configService.updateZoneConfig("keno_ladder", JsonUtils.toString(kenoLadderConfig));
            ZoneConfig.setKenoLadder(kenoLadderConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            kenoLadderRepository.updateOdds(ZoneConfig.getKenoLadder().getOdds());
        } catch (RuntimeException e) {
            log.error("키노사다리 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<KenoLadder> getComplete() {
        return kenoLadderRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<KenoLadder> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return kenoLadderRepository.findAll(builder, pageable);
    }

    @Override
    public KenoLadderDto.Score findScore(Long id) {
        KenoLadder kenoLadder = kenoLadderRepository.findOne(id);

        KenoLadderDto.Score score = new KenoLadderDto.Score();
        score.setId(kenoLadder.getId());
        score.setRound(kenoLadder.getRound());
        score.setGameDate(kenoLadder.getGameDate());
        score.setStart(kenoLadder.getStart());
        score.setLine(kenoLadder.getLine());
        score.setOddeven(kenoLadder.getOddeven());
        score.setCancel(kenoLadder.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getKenoLadderUrl() + "?sdate=" + kenoLadder.getSdate());
        if (json == null) return score;

        kenoLadder = JsonUtils.toModel(json, KenoLadder.class);
        if (kenoLadder == null) return score;

        // 봇에 결과가 있다면
        if (kenoLadder.isClosing()) {
            score.setOddeven(kenoLadder.getOddeven());
            score.setLine(kenoLadder.getLine());
            score.setStart(kenoLadder.getStart());

            if (!"ODD".equals(score.getOddeven()) && !"EVEN".equals(score.getOddeven())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(KenoLadderDto.Score score) {
        KenoLadder kenoLadder = kenoLadderRepository.findOne(score.getId());

        try {
            if (kenoLadder.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                kenoLadderGameService.rollbackPayment(kenoLadder);
            }

            // 스코어 입력
            kenoLadder.updateScore(score);
            kenoLadderRepository.saveAndFlush(kenoLadder);
            kenoLadderGameService.closingBetting(kenoLadder);
            kenoLadderBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("키노사다리 {} - {}회차 수동처리를 하지 못하였습니다. - {}", kenoLadder.getGameDate(), kenoLadder.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public AjaxResult closingAllGame() {
        QBetItem qi = QBetItem.betItem;
        int total = 0;
        int closing = 0;

        Iterable<KenoLadder> iterable = kenoLadderRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(5))));
        for (KenoLadder ladder : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.KENO_LADDER).and(qi.groupId.eq(ladder.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getKenoLadderUrl() + "?sdate=" + ladder.getSdate());
            if (json == null) continue;

            KenoLadder result = JsonUtils.toModel(json, KenoLadder.class);
            if (result == null) continue;

            if (result.isClosing()) {
                ladder.setStart(result.getStart());
                ladder.setLine(result.getLine());
                ladder.setOddeven(result.getOddeven());
                ladder.setClosing(true);
                kenoLadderRepository.saveAndFlush(ladder);
                closing++;
            }
        }
        kenoLadderBotService.checkResult();
        return new AjaxResult(true, "키노사다리 전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public KenoLadderDto.Config gameConfig() {
        KenoLadderDto.Config gameConfig = new KenoLadderDto.Config();
        KenoLadderConfig config = ZoneConfig.getKenoLadder();

        Date gameDate = config.getZoneMaker().getGameDate();
        KenoLadder kenoLadder = kenoLadderRepository.findOne(q.gameDate.eq(gameDate));

        if (kenoLadder == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isKenoLadder());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(kenoLadder.getGameDate());
        gameConfig.setSdate(kenoLadder.getSdate());
        gameConfig.setRound(kenoLadder.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(kenoLadder.getOdds());

        int betTime = (int) (kenoLadder.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setStart(config.isStart());
        gameConfig.setLine(config.isLine());
        gameConfig.setLineStart(config.isLineStart());

        return gameConfig;
    }

    @Override
    public Iterable<KenoLadderScore> getScore() {
        Iterable<KenoLadder> list = queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(6)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(KenoLadderScore::of).collect(Collectors.toList());
    }
}
