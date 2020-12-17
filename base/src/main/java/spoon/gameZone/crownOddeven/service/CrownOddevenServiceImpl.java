package spoon.gameZone.crownOddeven.service;

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
import spoon.gameZone.crownOddeven.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class CrownOddevenServiceImpl implements CrownOddevenService {

    private ConfigService configService;

    private MemberService memberService;

    private CrownOddevenGameService crownOddevenGameService;

    private CrownOddevenBotService crownOddevenBotService;

    private CrownOddevenRepository crownOddevenRepository;

    private BetItemRepository betItemRepository;

    private static QCrownOddeven q = QCrownOddeven.crownOddeven;

    @Transactional
    @Override
    public boolean updateConfig(CrownOddevenConfig crownOddevenConfig) {
        try {
            configService.updateZoneConfig("cw_oddeven", JsonUtils.toString(crownOddevenConfig));
            ZoneConfig.setCrownOddeven(crownOddevenConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            crownOddevenRepository.updateOdds(ZoneConfig.getCrownOddeven().getOdds());
        } catch (RuntimeException e) {
            log.error("홀짝 설정 변경에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<CrownOddeven> getComplete() {
        return crownOddevenRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<CrownOddeven> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return crownOddevenRepository.findAll(builder, pageable);
    }

    @Override
    public CrownOddevenDto.Score findScore(Long id) {
        CrownOddeven crownOddeven = crownOddevenRepository.findOne(id);

        CrownOddevenDto.Score score = new CrownOddevenDto.Score();
        score.setId(crownOddeven.getId());
        score.setRound(crownOddeven.getRound());
        score.setGameDate(crownOddeven.getGameDate());
        score.setCancel(crownOddeven.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getCrownOddevenUrl() + "?sdate=" + crownOddeven.getSdate());
        if (json == null) return score;

        crownOddeven = JsonUtils.toModel(json, CrownOddeven.class);
        if (crownOddeven == null) return score;

        // 봇에 결과가 있다면
        if (crownOddeven.isClosing()) {
            score.setCard1(crownOddeven.getCard1());
            score.setCard2(crownOddeven.getCard2());
            score.setOddeven(crownOddeven.getOddeven());
            score.setOverunder(crownOddeven.getOverunder());
            if ("cancel".equals(crownOddeven.getOddeven())) {
                score.setCancel(true);
            }
        }
        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(CrownOddevenDto.Score score) {
        CrownOddeven crownOddeven = crownOddevenRepository.findOne(score.getId());

        try {
            if (crownOddeven.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                crownOddevenGameService.rollbackPayment(crownOddeven);
            }

            crownOddeven.updateScore(score);

            crownOddevenRepository.saveAndFlush(crownOddeven);
            crownOddevenGameService.closingBetting(crownOddeven);
            crownOddevenBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("홀짝 수동처리를 하지 못하였습니다.", e);
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

        Iterable<CrownOddeven> iterable = crownOddevenRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(1))));
        for (CrownOddeven crownOddeven : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.CROWN_ODDEVEN).and(qi.groupId.eq(crownOddeven.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getCrownOddevenUrl() + "?sdate=" + crownOddeven.getSdate());
            if (json == null) continue;

            CrownOddeven result = JsonUtils.toModel(json, CrownOddeven.class);
            if (result == null) continue;

            if (result.isClosing()) {
                crownOddeven.setCard1(result.getCard1());
                crownOddeven.setCard2(result.getCard2());
                crownOddeven.setOddeven(result.getOddeven());
                crownOddeven.setOverunder(result.getOverunder());
                crownOddeven.setClosing(true);
                crownOddevenRepository.saveAndFlush(crownOddeven);
                closing++;
            }
        }
        crownOddevenBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public CrownOddevenDto.Config gameConfig() {
        CrownOddevenDto.Config gameConfig = new CrownOddevenDto.Config();
        CrownOddevenConfig config = ZoneConfig.getCrownOddeven();

        // 홀짝 바카라는 1분씩 댕겨 줘야 한다.
        Date gameDate = new Date(config.getZoneMaker().getGameDate().getTime() - 1000 * 60);
        CrownOddeven crownOddeven = crownOddevenRepository.findOne(q.gameDate.eq(gameDate));

        if (crownOddeven == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isCrownOddeven());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(crownOddeven.getGameDate());
        gameConfig.setSdate(crownOddeven.getSdate());
        gameConfig.setRound(crownOddeven.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(crownOddeven.getOdds());

        // 60초 보정
        int betTime = (int) (crownOddeven.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000 + 60000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setOverunder(config.isOverunder());

        return gameConfig;
    }
}
