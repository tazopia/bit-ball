package spoon.gameZone.snail.service;

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
import spoon.gameZone.snail.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class SnailServiceImpl implements SnailService {

    private ConfigService configService;

    private MemberService memberService;

    private SnailGameService snailGameService;

    private SnailBotService snailBotService;

    private SnailRepository snailRepository;

    private BetItemRepository betItemRepository;

    private static QSnail q = QSnail.snail;

    @Transactional
    @Override
    public boolean updateConfig(SnailConfig snailConfig) {
        try {
            configService.updateZoneConfig("snail", JsonUtils.toString(snailConfig));
            ZoneConfig.setSnail(snailConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            snailRepository.updateOdds(ZoneConfig.getSnail().getOdds());
        } catch (RuntimeException e) {
            log.error("달팽이 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Snail> getComplete() {
        return snailRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Snail> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return snailRepository.findAll(builder, pageable);
    }

    @Override
    public SnailDto.Score findScore(Long id) {
        Snail snail = snailRepository.findOne(id);

        SnailDto.Score score = new SnailDto.Score();
        score.setId(snail.getId());
        score.setRound(snail.getRound());
        score.setGameDate(snail.getGameDate());
        score.setResult(snail.getResult());
        score.setCancel(snail.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getSnailUrl() + "?sdate=" + snail.getSdate());
        if (json == null) return score;

        snail = JsonUtils.toModel(json, Snail.class);
        if (snail == null) return score;

        // 봇에 결과가 있다면
        if (snail.isClosing()) {
            score.setResult(snail.getResult().replaceAll(",", "-"));

            if (StringUtils.empty(score.getResult())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(SnailDto.Score score) {
        Snail snail = snailRepository.findOne(score.getId());

        try {
            if (snail.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                snailGameService.rollbackPayment(snail);
            }

            // 스코어 입력
            snail.updateScore(score);
            snailRepository.saveAndFlush(snail);
            snailGameService.closingBetting(snail);
            snailBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("달팽이 {} - {}회차 수동처리를 하지 못하였습니다. - {}", snail.getGameDate(), snail.getRound(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public AjaxResult closingAllGame() {
        QBetItem qi = QBetItem.betItem;
        int total = 0;
        int closing = 0;

        Iterable<Snail> iterable = snailRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(5))));
        for (Snail snail : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.SNAIL).and(qi.groupId.eq(snail.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getSnailUrl() + "?sdate=" + snail.getSdate());
            if (json == null) continue;

            Snail result = JsonUtils.toModel(json, Snail.class);
            if (result == null) continue;

            if (result.isClosing()) {
                snail.setResult(result.getResult());
                snail.setClosing(true);
                snailRepository.saveAndFlush(snail);
                closing++;
            }
        }
        snailBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public SnailDto.Config gameConfig() {
        SnailDto.Config gameConfig = new SnailDto.Config();
        SnailConfig config = ZoneConfig.getSnail();

        Date gameDate = config.getZoneMaker().getGameDate();
        Snail snail = snailRepository.findOne(q.gameDate.eq(gameDate));

        if (snail == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isLadder());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(snail.getGameDate());
        gameConfig.setSdate(snail.getSdate());
        gameConfig.setRound(snail.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(snail.getOdds());

        int betTime = (int) (snail.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;

        gameConfig.setBetTime(betTime);

        return gameConfig;
    }
}
