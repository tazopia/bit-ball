package spoon.gameZone.crownSutda.service;

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
import spoon.gameZone.crownSutda.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class CrownSutdaServiceImpl implements CrownSutdaService {

    private ConfigService configService;

    private MemberService memberService;

    private CrownSutdaBotService crownSutdaBotService;

    private CrownSutdaGameService crownSutdaGameService;

    private CrownSutdaRepository crownSutdaRepository;

    private BetItemRepository betItemRepository;

    private static QCrownSutda q = QCrownSutda.crownSutda;

    @Transactional
    @Override
    public boolean updateConfig(CrownSutdaConfig crownSutdaConfig) {
        try {
            configService.updateZoneConfig("cw_sutda", JsonUtils.toString(crownSutdaConfig));
            ZoneConfig.setCrownSutda(crownSutdaConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            crownSutdaRepository.updateOdds(ZoneConfig.getCrownSutda().getOdds());
        } catch (RuntimeException e) {
            log.error("섰다 설정 변경에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<CrownSutda> getComplete() {
        return crownSutdaRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<CrownSutda> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return crownSutdaRepository.findAll(builder, pageable);
    }

    @Override
    public CrownSutdaDto.Score findScore(Long id) {
        CrownSutda crownSutda = crownSutdaRepository.findOne(id);

        CrownSutdaDto.Score score = new CrownSutdaDto.Score();
        score.setId(crownSutda.getId());
        score.setRound(crownSutda.getRound());
        score.setGameDate(crownSutda.getGameDate());
        score.setKorea(crownSutda.getKorea());
        score.setJapan(crownSutda.getJapan());
        score.setK1(crownSutda.getK1());
        score.setK2(crownSutda.getK2());
        score.setJ1(crownSutda.getJ1());
        score.setJ2(crownSutda.getJ2());
        score.setResult(crownSutda.getResult());
        score.setCancel(crownSutda.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getCrownSutdaUrl() + "?sdate=" + crownSutda.getSdate());
        if (json == null) return score;

        crownSutda = JsonUtils.toModel(json, CrownSutda.class);
        if (crownSutda == null) return score;

        // 봇에 결과가 있다면
        if (crownSutda.isClosing()) {
            score.setKorea(crownSutda.getKorea());
            score.setJapan(crownSutda.getJapan());
            score.setK1(crownSutda.getK1());
            score.setK2(crownSutda.getK2());
            score.setJ1(crownSutda.getJ1());
            score.setJ2(crownSutda.getJ2());
            score.setResult(crownSutda.getResult());
            if ("C".equals(crownSutda.getResult())) {
                score.setCancel(true);
            }
        }
        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(CrownSutdaDto.Score score) {
        CrownSutda crownSutda = crownSutdaRepository.findOne(score.getId());

        try {
            if (crownSutda.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                crownSutdaGameService.rollbackPayment(crownSutda);
            }

            crownSutda.updateScore(score);

            crownSutdaRepository.saveAndFlush(crownSutda);
            crownSutdaGameService.closingBetting(crownSutda);
            crownSutdaBotService.checkResult();

        } catch (RuntimeException e) {
            log.error("섰다 수동처리를 하지 못하였습니다.", e);
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

        Iterable<CrownSutda> iterable = crownSutdaRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(1))));
        for (CrownSutda crownSutda : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.CROWN_SUTDA).and(qi.groupId.eq(crownSutda.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getCrownSutdaUrl() + "?sdate=" + crownSutda.getSdate());
            if (json == null) continue;

            CrownSutda result = JsonUtils.toModel(json, CrownSutda.class);
            if (result == null) continue;

            if (result.isClosing()) {
                crownSutda.setKorea(result.getKorea());
                crownSutda.setJapan(result.getJapan());
                crownSutda.setK1(result.getK1());
                crownSutda.setK2(result.getK2());
                crownSutda.setJ1(result.getJ1());
                crownSutda.setJ2(result.getJ2());
                crownSutda.setResult(result.getResult());
                if ("C".equals(result.getResult())) {
                    crownSutda.setCancel(true);
                }

                crownSutda.setClosing(true);
                crownSutdaRepository.saveAndFlush(crownSutda);
                closing++;
            }
        }

        crownSutdaBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public CrownSutdaDto.Config gameConfig() {
        CrownSutdaDto.Config gameConfig = new CrownSutdaDto.Config();
        CrownSutdaConfig config = ZoneConfig.getCrownSutda();

        // 홀짝 바카라는 1분씩 댕겨 줘야 한다.
        Date gameDate = new Date(config.getZoneMaker().getGameDate().getTime() - 1000 * 60);
        CrownSutda crownSutda = crownSutdaRepository.findOne(q.gameDate.eq(gameDate));

        if (crownSutda == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isCrownSutda());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(crownSutda.getGameDate());
        gameConfig.setSdate(crownSutda.getSdate());
        gameConfig.setRound(crownSutda.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(crownSutda.getOdds());

        // 60초 보정
        int betTime = (int) (crownSutda.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000 + 60000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        return gameConfig;
    }
}
