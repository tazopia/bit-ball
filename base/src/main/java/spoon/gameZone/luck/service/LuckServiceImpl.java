package spoon.gameZone.luck.service;

import com.querydsl.core.BooleanBuilder;
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
import spoon.gameZone.luck.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class LuckServiceImpl implements LuckService {

    private ConfigService configService;

    private MemberService memberService;

    private LuckGameService luckGameService;

    private LuckBotService luckBotService;

    private LuckRepository luckRepository;

    private BetItemRepository betItemRepository;

    private static QLuck q = QLuck.luck;

    @Transactional
    @Override
    public boolean updateConfig(LuckConfig luckConfig) {
        try {
            configService.updateZoneConfig("luck", JsonUtils.toString(luckConfig));
            ZoneConfig.setLuck(luckConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            luckRepository.updateOdds(ZoneConfig.getLuck().getOdds());
        } catch (RuntimeException e) {
            log.error("세븐럭 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Luck> getComplete() {
        return luckRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Luck> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return luckRepository.findAll(builder, pageable);
    }

    @Override
    public LuckDto.Score findScore(Long id) {
        Luck luck = luckRepository.findOne(id);

        LuckDto.Score score = new LuckDto.Score();
        score.setId(luck.getId());
        score.setRound(luck.getRound());
        score.setGameDate(luck.getGameDate());
        score.setCancel(luck.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getLuckUrl() + "?sdate=" + luck.getSdate());
        if (json == null) return score;

        luck = JsonUtils.toModel(json, Luck.class);
        if (luck == null) return score;

        // 봇에 결과가 있다면
        if (luck.isClosing()) {
            score.setDealer1(luck.getDealer().substring(0, 1));
            score.setDealer2(luck.getDealer().substring(1, 2));
            score.setPlayer11(luck.getPlayer1().substring(0, 1));
            score.setPlayer12(luck.getPlayer1().substring(1, 2));
            score.setPlayer21(luck.getPlayer2().substring(0, 1));
            score.setPlayer22(luck.getPlayer2().substring(1, 2));
            score.setPlayer31(luck.getPlayer3().substring(0, 1));
            score.setPlayer32(luck.getPlayer3().substring(1, 2));
            score.setResult(luck.getResult());
        }
        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(LuckDto.Score score) {
        Luck luck = luckRepository.findOne(score.getId());

        try {
            if (luck.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                luckGameService.rollbackPayment(luck);
            }

            // 스코어 입력
            luck.updateScore(score);
            luckRepository.saveAndFlush(luck);
            luckGameService.closingBetting(luck);
            luckBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("세븐럭 {} - {}회차 수동처리를 하지 못하였습니다. - {}", luck.getGameDate(), luck.getRound(), e.getMessage());
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

        Iterable<Luck> iterable = luckRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(2))));
        for (Luck luck : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.LUCK).and(qi.groupId.eq(luck.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getLuckUrl() + "?sdate=" + luck.getSdate());
            if (json == null) continue;

            Luck result = JsonUtils.toModel(json, Luck.class);
            if (result == null) continue;

            if (result.isClosing()) {
                luck.setDealer(result.getDealer());
                luck.setPlayer1(result.getPlayer1());
                luck.setPlayer2(result.getPlayer2());
                luck.setPlayer3(result.getPlayer3());
                luck.setResult(result.getResult());
                luck.setClosing(true);
                luckRepository.saveAndFlush(luck);
                closing++;
            }
        }
        luckBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public LuckDto.Config gameConfig() {
        LuckDto.Config gameConfig = new LuckDto.Config();
        LuckConfig config = ZoneConfig.getLuck();

        Date gameDate = config.getZoneMaker().getGameDate();
        Luck luck = luckRepository.findOne(q.gameDate.eq(gameDate));

        if (luck == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isLuck());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(luck.getGameDate());
        gameConfig.setSdate(luck.getSdate());
        gameConfig.setRound(luck.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(luck.getOdds());

        int betTime = (int) (luck.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setPlayer1(config.isPlayer1());
        gameConfig.setPlayer2(config.isPlayer2());
        gameConfig.setPlayer3(config.isPlayer3());
        gameConfig.setColor(config.isColor());
        gameConfig.setPattern(config.isPattern());

        return gameConfig;
    }
}
