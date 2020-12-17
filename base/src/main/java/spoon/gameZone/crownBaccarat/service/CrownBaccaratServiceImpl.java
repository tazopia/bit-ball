package spoon.gameZone.crownBaccarat.service;

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
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.config.service.ConfigService;
import spoon.game.domain.MenuCode;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.crownBaccarat.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class CrownBaccaratServiceImpl implements CrownBaccaratService {

    private ConfigService configService;

    private MemberService memberService;

    private CrownBaccaratBotService crownBaccaratBotService;

    private CrownBaccaratGameService crownBaccaratGameService;

    private CrownBaccaratRepository crownBaccaratRepository;

    private BetItemRepository betItemRepository;

    private static QCrownBaccarat q = QCrownBaccarat.crownBaccarat;

    @Transactional
    @Override
    public boolean updateConfig(CrownBaccaratConfig crownBaccaratConfig) {
        try {
            configService.updateZoneConfig("cw_baccarat", JsonUtils.toString(crownBaccaratConfig));
            ZoneConfig.setCrownBaccarat(crownBaccaratConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            crownBaccaratRepository.updateOdds(ZoneConfig.getCrownBaccarat().getOdds());
        } catch (RuntimeException e) {
            log.error("바카라 설정 변경에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<CrownBaccarat> getComplete() {
        return crownBaccaratRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<CrownBaccarat> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return crownBaccaratRepository.findAll(builder, pageable);
    }

    @Override
    public CrownBaccaratDto.Score findScore(Long id) {
        CrownBaccarat crownBaccarat = crownBaccaratRepository.findOne(id);

        CrownBaccaratDto.Score score = new CrownBaccaratDto.Score();
        score.setId(crownBaccarat.getId());
        score.setRound(crownBaccarat.getRound());
        score.setGameDate(crownBaccarat.getGameDate());
        score.setP(crownBaccarat.getP());
        score.setB(crownBaccarat.getB());
        score.setC(crownBaccarat.getC());
        score.setResult(crownBaccarat.getResult());
        score.setCancel(crownBaccarat.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getCrownBaccaratUrl() + "?sdate=" + crownBaccarat.getSdate());
        if (json == null) return score;

        crownBaccarat = JsonUtils.toModel(json, CrownBaccarat.class);
        if (crownBaccarat == null) return score;

        // 봇에 결과가 있다면
        if (crownBaccarat.isClosing()) {
            score.setP(crownBaccarat.getP());
            score.setB(crownBaccarat.getB());
            score.setC(crownBaccarat.getC());
            score.setResult(crownBaccarat.getResult());
            if ("C".equals(crownBaccarat.getResult())) {
                score.setCancel(true);
            }
        }
        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(CrownBaccaratDto.Score score) {
        CrownBaccarat crownBaccarat = crownBaccaratRepository.findOne(score.getId());

        try {
            if (crownBaccarat.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                crownBaccaratGameService.rollbackPayment(crownBaccarat);
            }

            crownBaccarat.updateScore(score);

            crownBaccaratRepository.saveAndFlush(crownBaccarat);
            crownBaccaratGameService.closingBetting(crownBaccarat);
            crownBaccaratBotService.checkResult();

        } catch (RuntimeException e) {
            log.error("바카라 수동처리를 하지 못하였습니다.", e);
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

        Iterable<CrownBaccarat> iterable = crownBaccaratRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(1))));
        for (CrownBaccarat crownBaccarat : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.CROWN_BACCARAT).and(qi.groupId.eq(crownBaccarat.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getCrownBaccaratUrl() + "?sdate=" + crownBaccarat.getSdate());
            if (json == null) continue;

            CrownBaccarat result = JsonUtils.toModel(json, CrownBaccarat.class);
            if (result == null) continue;

            if (result.isClosing()) {
                crownBaccarat.setP(result.getP());
                crownBaccarat.setB(result.getB());
                crownBaccarat.setC(result.getC());
                crownBaccarat.setResult(result.getResult());
                if ("C".equals(result.getResult())) {
                    crownBaccarat.setCancel(true);
                }

                crownBaccarat.setClosing(true);
                crownBaccaratRepository.saveAndFlush(crownBaccarat);
                closing++;
            }
        }

        crownBaccaratBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public CrownBaccaratDto.Config gameConfig() {
        CrownBaccaratDto.Config gameConfig = new CrownBaccaratDto.Config();
        CrownBaccaratConfig config = ZoneConfig.getCrownBaccarat();

        // 홀짝 바카라는 1분씩 댕겨 줘야 한다.
        Date gameDate = new Date(config.getZoneMaker().getGameDate().getTime() - 1000 * 60);
        CrownBaccarat crownBaccarat = crownBaccaratRepository.findOne(q.gameDate.eq(gameDate));

        if (crownBaccarat == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isCrownBaccarat());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(crownBaccarat.getGameDate());
        gameConfig.setSdate(crownBaccarat.getSdate());
        gameConfig.setRound(crownBaccarat.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(crownBaccarat.getOdds());

        // 60초 보정
        int betTime = (int) (crownBaccarat.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000 + 60000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        return gameConfig;
    }
}
