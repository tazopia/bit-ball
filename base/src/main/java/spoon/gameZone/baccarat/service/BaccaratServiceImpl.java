package spoon.gameZone.baccarat.service;

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
import spoon.gameZone.baccarat.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.time.zone.ZoneRulesException;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class BaccaratServiceImpl implements BaccaratService {

    private ConfigService configService;

    private MemberService memberService;

    private BaccaratBotService baccaratBotService;

    private BaccaratGameService baccaratGameService;

    private BaccaratRepository baccaratRepository;

    private BetItemRepository betItemRepository;

    private static QBaccarat q = QBaccarat.baccarat;

    @Transactional
    @Override
    public boolean updateConfig(BaccaratConfig baccaratConfig) {
        try {
            configService.updateZoneConfig("baccarat", JsonUtils.toString(baccaratConfig));
            ZoneConfig.setBaccarat(baccaratConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            baccaratRepository.updateOdds(ZoneConfig.getBaccarat().getOdds());
        } catch (RuntimeException e) {
            log.error("바카라 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Baccarat> getComplete() {
        return baccaratRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Baccarat> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return baccaratRepository.findAll(builder, pageable);
    }

    @Override
    public BaccaratDto.Score findScore(Long id) {
        Baccarat baccarat = baccaratRepository.findOne(id);

        BaccaratDto.Score score = new BaccaratDto.Score();
        score.setId(baccarat.getId());
        score.setRound(baccarat.getRound());
        score.setGameDate(baccarat.getGameDate());
        score.convertCard(baccarat.getP1(), baccarat.getP2(), baccarat.getP3(), baccarat.getB1(), baccarat.getB2(), baccarat.getB3());
        score.setCancel(baccarat.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBaccaratUrl() + "?sdate=" + baccarat.getSdate());
        if (json == null) return score;

        baccarat = JsonUtils.toModel(json, Baccarat.class);
        if (baccarat == null) return score;

        // 봇에 결과가 있다면
        if (baccarat.isClosing()) {
            score.convertCard(baccarat.getP1(), baccarat.getP2(), baccarat.getP3(), baccarat.getB1(), baccarat.getB2(), baccarat.getB3());
            if (StringUtils.empty(baccarat.getP1())) {
                score.setCancel(true);
            }
        }
        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(BaccaratDto.Score score) {
        Baccarat baccarat = baccaratRepository.findOne(score.getId());

        try {
            // 스코어 입력
            boolean success = score.convertCard();
            if (!success) {
                throw new ZoneRulesException("결과를 변환하지 못하였습니다.");
            }

            if (baccarat.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                baccaratGameService.rollbackPayment(baccarat);
            }

            baccarat.updateScore(score);

            baccaratRepository.saveAndFlush(baccarat);
            baccaratGameService.closingBetting(baccarat);
            baccaratBotService.checkResult();

        } catch (RuntimeException e) {
            log.error("바카라 {} - {}회차 수동처리를 하지 못하였습니다. - {}", baccarat.getGameDate(), baccarat.getRound(), e.getMessage());
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

        Iterable<Baccarat> iterable = baccaratRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(1))));
        for (Baccarat baccarat : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.BACCARAT).and(qi.groupId.eq(baccarat.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBaccaratUrl() + "?sdate=" + baccarat.getSdate());
            if (json == null) continue;

            Baccarat result = JsonUtils.toModel(json, Baccarat.class);
            if (result == null) continue;

            if (result.isClosing()) {
                baccarat.setP1(result.getP1());
                baccarat.setP2(result.getP2());
                baccarat.setP3(result.getP3());
                baccarat.setB1(result.getB1());
                baccarat.setB2(result.getB2());
                baccarat.setB3(result.getB3());
                baccarat.setResult(result.getResult());

                baccarat.setClosing(true);
                baccaratRepository.saveAndFlush(baccarat);
                closing++;
            }
        }

        baccaratBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public BaccaratDto.Config gameConfig() {
        BaccaratDto.Config gameConfig = new BaccaratDto.Config();
        BaccaratConfig config = ZoneConfig.getBaccarat();

        // 홀짝 바카라는 1분씩 댕겨 줘야 한다.
        Date gameDate = new Date(config.getZoneMaker().getGameDate().getTime() - 1000 * 60);
        Baccarat baccarat = baccaratRepository.findOne(q.gameDate.eq(gameDate));

        if (baccarat == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isOddeven());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(baccarat.getGameDate());
        gameConfig.setSdate(baccarat.getSdate());
        gameConfig.setRound(baccarat.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(baccarat.getOdds());

        // 60초 보정
        int betTime = (int) (baccarat.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000 + 60000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        return gameConfig;
    }
}
