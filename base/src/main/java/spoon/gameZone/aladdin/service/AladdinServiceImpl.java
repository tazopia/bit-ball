package spoon.gameZone.aladdin.service;

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
import spoon.gameZone.aladdin.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class AladdinServiceImpl implements AladdinService {

    private ConfigService configService;

    private MemberService memberService;

    private AladdinGameService aladdinGameService;

    private AladdinBotService aladdinBotService;

    private AladdinRepository aladdinRepository;

    private BetItemRepository betItemRepository;

    private static QAladdin q = QAladdin.aladdin;

    @Transactional
    @Override
    public boolean updateConfig(AladdinConfig aladdinConfig) {
        try {
            configService.updateZoneConfig("aladdin", JsonUtils.toString(aladdinConfig));
            ZoneConfig.setAladdin(aladdinConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            aladdinRepository.updateOdds(ZoneConfig.getAladdin().getOdds());
        } catch (RuntimeException e) {
            log.error("알라딘 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Aladdin> getComplete() {
        return aladdinRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Aladdin> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return aladdinRepository.findAll(builder, pageable);
    }

    @Override
    public AladdinDto.Score findScore(Long id) {
        Aladdin aladdin = aladdinRepository.findOne(id);

        AladdinDto.Score score = new AladdinDto.Score();
        score.setId(aladdin.getId());
        score.setRound(aladdin.getRound());
        score.setGameDate(aladdin.getGameDate());
        score.setStart(aladdin.getStart());
        score.setLine(aladdin.getLine());
        score.setOddeven(aladdin.getOddeven());
        score.setCancel(aladdin.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getAladdinUrl() + "?sdate=" + aladdin.getSdate());
        if (json == null) return score;

        aladdin = JsonUtils.toModel(json, Aladdin.class);
        if (aladdin == null) return score;

        // 봇에 결과가 있다면
        if (aladdin.isClosing()) {
            score.setOddeven(aladdin.getOddeven());
            score.setLine(aladdin.getLine());
            score.setStart(aladdin.getStart());

            if (!"ODD".equals(score.getOddeven()) && !"EVEN".equals(score.getOddeven())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(AladdinDto.Score score) {
        Aladdin aladdin = aladdinRepository.findOne(score.getId());

        try {
            if (aladdin.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                aladdinGameService.rollbackPayment(aladdin);
            }

            // 스코어 입력
            aladdin.updateScore(score);
            aladdinRepository.saveAndFlush(aladdin);
            aladdinGameService.closingBetting(aladdin);
            aladdinBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("알라딘 {} - {}회차 수동처리를 하지 못하였습니다. - {}", aladdin.getGameDate(), aladdin.getRound(), e.getMessage());
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

        Iterable<Aladdin> iterable = aladdinRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(3))));
        for (Aladdin aladdin : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.ALADDIN).and(qi.groupId.eq(aladdin.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getAladdinUrl() + "?sdate=" + aladdin.getSdate());
            if (json == null) continue;

            Aladdin result = JsonUtils.toModel(json, Aladdin.class);
            if (result == null) continue;

            if (result.isClosing()) {
                aladdin.setStart(result.getStart());
                aladdin.setLine(result.getLine());
                aladdin.setOddeven(result.getOddeven());
                aladdin.setClosing(true);
                aladdinRepository.saveAndFlush(aladdin);
                closing++;
            }
        }

        aladdinBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public AladdinDto.Config gameConfig() {
        AladdinDto.Config gameConfig = new AladdinDto.Config();
        AladdinConfig config = ZoneConfig.getAladdin();

        Date gameDate = config.getZoneMaker().getGameDate();
        Aladdin aladdin = aladdinRepository.findOne(q.gameDate.eq(gameDate));

        if (aladdin == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isAladdin());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(aladdin.getGameDate());
        gameConfig.setSdate(aladdin.getSdate());
        gameConfig.setRound(aladdin.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(aladdin.getOdds());

        int betTime = (int) (aladdin.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setStart(config.isStart());
        gameConfig.setLine(config.isLine());
        gameConfig.setLineStart(config.isLineStart());

        return gameConfig;
    }
}
