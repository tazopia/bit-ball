package spoon.gameZone.dari.service;

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
import spoon.gameZone.dari.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class DariServiceImpl implements DariService {

    private ConfigService configService;

    private MemberService memberService;

    private DariGameService dariGameService;

    private DariBotService dariBotService;

    private DariRepository dariRepository;

    private BetItemRepository betItemRepository;

    private static QDari q = QDari.dari;

    @Transactional
    @Override
    public boolean updateConfig(DariConfig dariConfig) {
        try {
            configService.updateZoneConfig("dari", JsonUtils.toString(dariConfig));
            ZoneConfig.setDari(dariConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            dariRepository.updateOdds(ZoneConfig.getDari().getOdds());
        } catch (RuntimeException e) {
            log.error("다리다리 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Dari> getComplete() {
        return dariRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Dari> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return dariRepository.findAll(builder, pageable);
    }

    @Override
    public DariDto.Score findScore(Long id) {
        Dari dari = dariRepository.findOne(id);

        DariDto.Score score = new DariDto.Score();
        score.setId(dari.getId());
        score.setRound(dari.getRound());
        score.setGameDate(dari.getGameDate());
        score.setStart(dari.getStart());
        score.setLine(dari.getLine());
        score.setOddeven(dari.getOddeven());
        score.setCancel(dari.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getDariUrl() + "?sdate=" + dari.getSdate());
        if (json == null) return score;

        dari = JsonUtils.toModel(json, Dari.class);
        if (dari == null) return score;

        // 봇에 결과가 있다면
        if (dari.isClosing()) {
            score.setOddeven(dari.getOddeven());
            score.setLine(dari.getLine());
            score.setStart(dari.getStart());

            if (!"ODD".equals(score.getOddeven()) && !"EVEN".equals(score.getOddeven())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(DariDto.Score score) {
        Dari dari = dariRepository.findOne(score.getId());

        try {
            if (dari.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                dariGameService.rollbackPayment(dari);
            }

            // 스코어 입력
            dari.updateScore(score);
            dariRepository.saveAndFlush(dari);
            dariGameService.closingBetting(dari);
            dariBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("다리다리 {} - {}회차 수동처리를 하지 못하였습니다. - {}", dari.getGameDate(), dari.getRound(), e.getMessage());
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

        Iterable<Dari> iterable = dariRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(3))));
        for (Dari dari : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.DARI).and(qi.groupId.eq(dari.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getDariUrl() + "?sdate=" + dari.getSdate());
            if (json == null) continue;

            Dari result = JsonUtils.toModel(json, Dari.class);
            if (result == null) continue;

            if (result.isClosing()) {
                dari.setStart(result.getStart());
                dari.setLine(result.getLine());
                dari.setOddeven(result.getOddeven());
                dari.setClosing(true);
                dariRepository.saveAndFlush(dari);
                closing++;
            }
        }
        dariBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public DariDto.Config gameConfig() {
        DariDto.Config gameConfig = new DariDto.Config();
        DariConfig config = ZoneConfig.getDari();

        Date gameDate = config.getZoneMaker().getGameDate();
        Dari dari = dariRepository.findOne(q.gameDate.eq(gameDate));

        if (dari == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isDari());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(dari.getGameDate());
        gameConfig.setSdate(dari.getSdate());
        gameConfig.setRound(dari.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(dari.getOdds());

        int betTime = (int) (dari.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setStart(config.isStart());
        gameConfig.setLine(config.isLine());
        gameConfig.setLineStart(config.isLineStart());

        return gameConfig;
    }
}
