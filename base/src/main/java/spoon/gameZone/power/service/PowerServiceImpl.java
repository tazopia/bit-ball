package spoon.gameZone.power.service;

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
import spoon.gameZone.power.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Service
public class PowerServiceImpl implements PowerService {

    private ConfigService configService;

    private MemberService memberService;

    private PowerGameService powerGameService;

    private PowerBotService powerBotService;

    private PowerRepository powerRepository;

    private BetItemRepository betItemRepository;

    private static QPower q = QPower.power;

    private JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public boolean updateConfig(PowerConfig powerConfig) {
        try {
            configService.updateZoneConfig("power", JsonUtils.toString(powerConfig));
            ZoneConfig.setPower(powerConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            powerRepository.updateOdds(ZoneConfig.getPower().getOdds());
        } catch (RuntimeException e) {
            log.error("파워볼 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Power> getComplete() {
        return powerRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Power> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return powerRepository.findAll(builder, pageable);
    }

    @Override
    public PowerDto.Score findScore(Long id) {
        Power power = powerRepository.findOne(id);

        PowerDto.Score score = new PowerDto.Score();
        score.setId(power.getId());
        score.setRound(power.getRound());
        score.setTimes(power.getTimes());
        score.setGameDate(power.getGameDate());
        score.setPb(power.getPb());
        score.setBall(power.getBall());
        score.setCancel(power.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getPowerUrl() + "?sdate=" + power.getSdate());
        if (json == null) return score;

        power = JsonUtils.toModel(json, Power.class);
        if (power == null) return score;

        // 봇에 결과가 있다면
        if (power.isClosing()) {
            score.setPb(power.getPb());
            score.setBall(power.getBall());

            if (StringUtils.empty(power.getPb())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(PowerDto.Score score) {
        Power power = powerRepository.findOne(score.getId());

        try {
            if (power.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                powerGameService.rollbackPayment(power);
            }

            // 스코어 입력
            power.updateScore(score);
            powerRepository.saveAndFlush(power);
            powerGameService.closingBetting(power);
            powerBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("파워볼 {} - {}회차 수동처리를 하지 못하였습니다.", power.getGameDate(), power.getRound(), e);
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

        Iterable<Power> iterable = powerRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(5))));
        for (Power power : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.POWER).and(qi.groupId.eq(power.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getPowerUrl() + "?sdate=" + power.getSdate());
            if (json == null) continue;

            Power result = JsonUtils.toModel(json, Power.class);
            if (result == null) continue;

            if (result.isClosing()) {
                power.setOddeven(result.getOddeven());
                power.setPb_oddeven(result.getPb_oddeven());
                power.setOverunder(result.getOverunder());
                power.setPb_overunder(result.getPb_overunder());
                power.setSize(result.getSize());
                power.setSum(result.getSum());
                power.setPb(result.getPb());
                power.setBall(result.getBall());

                power.setClosing(true);
                powerRepository.saveAndFlush(power);
                closing++;
            }
        }
        powerBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public PowerDto.Config gameConfig() {
        PowerDto.Config gameConfig = new PowerDto.Config();
        PowerConfig config = ZoneConfig.getPower();

        // 파워볼은 현재 회차에서 1을 더해준다.
        Date gameDate = config.getZoneMaker().getGameDate();
        Power power = powerRepository.findOne(q.gameDate.eq(gameDate));

        if (power == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isPower());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(power.getGameDate());
        gameConfig.setSdate(power.getSdate());
        gameConfig.setRound(power.getRound());
        gameConfig.setTimes(power.getTimes());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(power.getOdds());

        int betTime = (int) (power.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
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

    @Override
    public List<PowerScore> getScore() {
        Iterable<Power> list = queryFactory.select(q).from(q)
                .where(q.closing.isTrue())
                .orderBy(q.sdate.desc())
                .limit(5)
                .fetch();
        return StreamSupport.stream(list.spliterator(), false).map(PowerScore::of).collect(Collectors.toList());
    }
}
