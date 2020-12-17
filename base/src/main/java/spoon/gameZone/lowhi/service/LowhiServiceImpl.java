package spoon.gameZone.lowhi.service;

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
import spoon.gameZone.lowhi.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class LowhiServiceImpl implements LowhiService {

    private ConfigService configService;

    private MemberService memberService;

    private LowhiGameService lowhiGameService;

    private LowhiBotService lowhiBotService;

    private LowhiRepository lowhiRepository;

    private BetItemRepository betItemRepository;

    private static QLowhi q = QLowhi.lowhi1;

    @Transactional
    @Override
    public boolean updateConfig(LowhiConfig lowhiConfig) {
        try {
            configService.updateZoneConfig("lowhi", JsonUtils.toString(lowhiConfig));
            ZoneConfig.setLowhi(lowhiConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            lowhiRepository.updateOdds(ZoneConfig.getLowhi().getOdds());
        } catch (RuntimeException e) {
            log.error("로하이 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Lowhi> getComplete() {
        return lowhiRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Lowhi> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return lowhiRepository.findAll(builder, pageable);
    }

    @Override
    public LowhiDto.Score findScore(Long id) {
        Lowhi lowhi = lowhiRepository.findOne(id);

        LowhiDto.Score score = new LowhiDto.Score();
        score.setId(lowhi.getId());
        score.setRound(lowhi.getRound());
        score.setGameDate(lowhi.getGameDate());
        score.setLowhi(lowhi.getLowhi());
        score.setOddeven(lowhi.getOddeven());
        score.setCancel(lowhi.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getLowhiUrl() + "?sdate=" + lowhi.getSdate());
        if (json == null) return score;

        lowhi = JsonUtils.toModel(json, Lowhi.class);
        if (lowhi == null) return score;

        // 봇에 결과가 있다면
        if (lowhi.isClosing()) {
            score.setOddeven(lowhi.getOddeven());
            score.setLowhi(lowhi.getLowhi());

            if (!"ODD".equals(score.getOddeven()) && !"EVEN".equals(score.getOddeven())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(LowhiDto.Score score) {
        Lowhi lowhi = lowhiRepository.findOne(score.getId());

        try {
            if (lowhi.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                lowhiGameService.rollbackPayment(lowhi);
            }

            // 스코어 입력
            lowhi.updateScore(score);
            lowhiRepository.saveAndFlush(lowhi);
            lowhiGameService.closingBetting(lowhi);
            lowhiBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("로하이 {} - {}회차 수동처리를 하지 못하였습니다. - {}", lowhi.getGameDate(), lowhi.getRound(), e.getMessage());
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

        Iterable<Lowhi> iterable = lowhiRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(3))));
        for (Lowhi lowhi : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.LOWHI).and(qi.groupId.eq(lowhi.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getLowhiUrl() + "?sdate=" + lowhi.getSdate());
            if (json == null) continue;

            Lowhi result = JsonUtils.toModel(json, Lowhi.class);
            if (result == null) continue;

            if (result.isClosing()) {
                lowhi.setLowhi(result.getLowhi());
                lowhi.setOddeven(result.getOddeven());
                lowhi.setClosing(true);
                lowhiRepository.saveAndFlush(lowhi);
                closing++;
            }
        }
        lowhiBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public LowhiDto.Config gameConfig() {
        LowhiDto.Config gameConfig = new LowhiDto.Config();
        LowhiConfig config = ZoneConfig.getLowhi();

        Date gameDate = config.getZoneMaker().getGameDate();
        Lowhi lowhi = lowhiRepository.findOne(q.gameDate.eq(gameDate));

        if (lowhi == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isLowhi());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(lowhi.getGameDate());
        gameConfig.setSdate(lowhi.getSdate());
        gameConfig.setRound(lowhi.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(lowhi.getOdds());

        int betTime = (int) (lowhi.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setLowhi(config.isLowhi());
        gameConfig.setLowhiOddeven(config.isLowhiOddeven());

        return gameConfig;
    }
}
