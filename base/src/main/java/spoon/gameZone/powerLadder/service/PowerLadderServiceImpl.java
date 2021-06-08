package spoon.gameZone.powerLadder.service;

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
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.powerLadder.*;
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
public class PowerLadderServiceImpl implements PowerLadderService {

    private ConfigService configService;

    private MemberService memberService;

    private PowerLadderGameService powerLadderGameService;

    private PowerLadderBotService powerLadderBotService;

    private PowerLadderRepository powerLadderRepository;

    private BetItemRepository betItemRepository;

    private JPAQueryFactory queryFactory;

    private static QPowerLadder q = QPowerLadder.powerLadder;

    @Transactional
    @Override
    public boolean updateConfig(PowerLadderConfig powerLadderConfig) {
        try {
            configService.updateZoneConfig("power_ladder", JsonUtils.toString(powerLadderConfig));
            ZoneConfig.setPowerLadder(powerLadderConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            powerLadderRepository.updateOdds(ZoneConfig.getPowerLadder().getOdds());
        } catch (RuntimeException e) {
            log.error("파워사다리 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<PowerLadder> getComplete() {
        return powerLadderRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<PowerLadder> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return powerLadderRepository.findAll(builder, pageable);
    }

    @Override
    public PowerLadderDto.Score findScore(Long id) {
        PowerLadder powerLadder = powerLadderRepository.findOne(id);

        PowerLadderDto.Score score = new PowerLadderDto.Score();
        score.setId(powerLadder.getId());
        score.setRound(powerLadder.getRound());
        score.setTimes(powerLadder.getTimes());
        score.setGameDate(powerLadder.getGameDate());
        score.setStart(powerLadder.getStart());
        score.setLine(powerLadder.getLine());
        score.setOddeven(powerLadder.getOddeven());
        score.setCancel(powerLadder.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getPowerLadderUrl() + "?sdate=" + powerLadder.getSdate());
        if (json == null) return score;

        powerLadder = JsonUtils.toModel(json, PowerLadder.class);
        if (powerLadder == null) return score;

        // 봇에 결과가 있다면
        if (powerLadder.isClosing()) {
            score.setOddeven(powerLadder.getOddeven());
            score.setLine(powerLadder.getLine());
            score.setStart(powerLadder.getStart());

            if (!"ODD".equals(score.getOddeven()) && !"EVEN".equals(score.getOddeven())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(PowerLadderDto.Score score) {
        PowerLadder powerLadder = powerLadderRepository.findOne(score.getId());

        try {
            if (powerLadder.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                powerLadderGameService.rollbackPayment(powerLadder);
            }

            // 스코어 입력
            powerLadder.updateScore(score);
            powerLadderRepository.saveAndFlush(powerLadder);
            powerLadderGameService.closingBetting(powerLadder);
            powerLadderBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("파워사다리 {} - {}회차 수동처리를 하지 못하였습니다. - {}", powerLadder.getGameDate(), powerLadder.getRound(), e.getMessage());
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

        Iterable<PowerLadder> iterable = powerLadderRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(5))));
        for (PowerLadder powerLadder : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.POWER_LADDER).and(qi.groupId.eq(powerLadder.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getPowerLadderUrl() + "?sdate=" + powerLadder.getSdate());
            if (json == null) continue;

            PowerLadder result = JsonUtils.toModel(json, PowerLadder.class);
            if (result == null) continue;

            if (result.isClosing()) {
                powerLadder.setStart(result.getStart());
                powerLadder.setLine(result.getLine());
                powerLadder.setOddeven(result.getOddeven());
                powerLadder.setClosing(true);
                powerLadderRepository.saveAndFlush(powerLadder);
                closing++;
            }
        }
        powerLadderBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public PowerLadderDto.Config gameConfig() {
        PowerLadderDto.Config gameConfig = new PowerLadderDto.Config();
        PowerLadderConfig config = ZoneConfig.getPowerLadder();

        // 파워볼은 현재 회차에서 1을 더해준다.
        Date gameDate = ZoneConfig.getPowerLadder().getZoneMaker().getGameDate();
        PowerLadder powerLadder = powerLadderRepository.findOne(q.gameDate.eq(gameDate));

        if (powerLadder == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isPowerLadder());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(powerLadder.getGameDate());
        gameConfig.setSdate(powerLadder.getSdate());
        gameConfig.setRound(powerLadder.getRound());
        gameConfig.setTimes(powerLadder.getTimes());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(powerLadder.getOdds());

        int betTime = (int) (powerLadder.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setStart(config.isStart());
        gameConfig.setLine(config.isLine());
        gameConfig.setLineStart(config.isLineStart());

        return gameConfig;
    }

    @Override
    public Iterable<PowerLadderScore> getScore() {
        Iterable<PowerLadder> list =  queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(6)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(PowerLadderScore::of).collect(Collectors.toList());
    }
}
