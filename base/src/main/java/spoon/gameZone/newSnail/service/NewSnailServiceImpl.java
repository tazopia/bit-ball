package spoon.gameZone.newSnail.service;

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
import spoon.gameZone.newSnail.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class NewSnailServiceImpl implements NewSnailService {

    private ConfigService configService;

    private MemberService memberService;

    private NewSnailGameService newSnailGameService;

    private NewSnailBotService newSnailBotService;

    private NewSnailRepository newSnailRepository;

    private BetItemRepository betItemRepository;

    private static QNewSnail q = QNewSnail.newSnail;

    @Transactional
    @Override
    public boolean updateConfig(NewSnailConfig newSnailConfig) {
        try {
            configService.updateZoneConfig("new_snail", JsonUtils.toString(newSnailConfig));
            ZoneConfig.setNewSnail(newSnailConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            newSnailRepository.updateOdds(ZoneConfig.getNewSnail().getOdds());
        } catch (RuntimeException e) {
            log.error("NEW 달팽이 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<NewSnail> getComplete() {
        return newSnailRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<NewSnail> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return newSnailRepository.findAll(builder, pageable);
    }

    @Override
    public NewSnailDto.Score findScore(Long id) {
        NewSnail newSnail = newSnailRepository.findOne(id);

        NewSnailDto.Score score = new NewSnailDto.Score();
        score.setId(newSnail.getId());
        score.setRound(newSnail.getRound());
        score.setGameDate(newSnail.getGameDate());
        score.setRanking(newSnail.getRanking());
        score.setCancel(newSnail.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getNewSnailUrl() + "?sdate=" + newSnail.getSdate());
        if (json == null) return score;

        newSnail = JsonUtils.toModel(json, NewSnail.class);
        if (newSnail == null) return score;

        // 봇에 결과가 있다면
        if (newSnail.isClosing()) {
            score.setRanking(newSnail.getRanking());

            if (StringUtils.empty(score.getRanking())) {
                score.setCancel(true);
            }
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(NewSnailDto.Score score) {
        NewSnail newSnail = newSnailRepository.findOne(score.getId());

        try {
            if (newSnail.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                newSnailGameService.rollbackPayment(newSnail);
            }

            // 스코어 입력
            newSnail.updateScore(score);
            newSnailRepository.saveAndFlush(newSnail);
            newSnailGameService.closingBetting(newSnail);
            newSnailBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("NEW 달팽이 {} - {}회차 수동처리를 하지 못하였습니다. - {}", newSnail.getGameDate(), newSnail.getRound(), e.getMessage());
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

        Iterable<NewSnail> iterable = newSnailRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(3))));
        for (NewSnail newSnail : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.NEW_SNAIL).and(qi.groupId.eq(newSnail.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getNewSnailUrl() + "?sdate=" + newSnail.getSdate());
            if (json == null) continue;

            NewSnail result = JsonUtils.toModel(json, NewSnail.class);
            if (result == null) continue;

            if (result.isClosing()) {
                newSnail.setRanking(result.getRanking());
                newSnail.setOe(result.getOe());
                newSnail.setOu(result.getOu());
                newSnail.setClosing(true);
                newSnail.setCancel(result.isCancel());
                newSnailRepository.saveAndFlush(newSnail);
                closing++;
            }
        }
        newSnailBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public NewSnailDto.Config gameConfig() {
        NewSnailDto.Config gameConfig = new NewSnailDto.Config();
        NewSnailConfig config = ZoneConfig.getNewSnail();

        Date gameDate = config.getZoneMaker().getGameDate();
        NewSnail newSnail = newSnailRepository.findOne(q.gameDate.eq(gameDate));

        if (newSnail == null) {
            gameConfig.setGameDate(gameDate);
            gameConfig.setRound(config.getZoneMaker().getRound());
            return gameConfig;
        }

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isNewSnail());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setGameDate(newSnail.getGameDate());
        gameConfig.setSdate(newSnail.getSdate());
        gameConfig.setRound(newSnail.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(newSnail.getOdds());

        int betTime = (int) (newSnail.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setOverunder(config.isOverunder());
        gameConfig.setRanking(config.isRanking());

        return gameConfig;
    }
}
