package spoon.gameZone.oddeven.service;

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
import spoon.gameZone.oddeven.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.time.zone.ZoneRulesException;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class OddevenServiceImpl implements OddevenService {

    private ConfigService configService;

    private MemberService memberService;

    private OddevenGameService oddevenGameService;

    private OddevenBotService oddevenBotService;

    private OddevenRepository oddevenRepository;

    private BetItemRepository betItemRepository;

    private static QOddeven q = QOddeven.oddeven1;

    @Transactional
    @Override
    public boolean updateConfig(OddevenConfig oddevenConfig) {
        try {
            configService.updateZoneConfig("oddeven", JsonUtils.toString(oddevenConfig));
            ZoneConfig.setOddeven(oddevenConfig);
            // 이미 등록된 게임의 배당을 변경한다.
            oddevenRepository.updateOdds(ZoneConfig.getOddeven().getOdds());
        } catch (RuntimeException e) {
            log.error("홀짝 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Oddeven> getComplete() {
        return oddevenRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Oddeven> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.round.eq(command.getRound()));
        }

        return oddevenRepository.findAll(builder, pageable);
    }

    @Override
    public OddevenDto.Score findScore(Long id) {
        Oddeven oddeven = oddevenRepository.findOne(id);

        OddevenDto.Score score = new OddevenDto.Score();
        score.setId(oddeven.getId());
        score.setRound(oddeven.getRound());
        score.setGameDate(oddeven.getGameDate());
        score.convertCard(oddeven.getCard1(), oddeven.getCard2());
        score.setCancel(oddeven.isCancel());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getOddevenUrl() + "?sdate=" + oddeven.getSdate());
        if (json == null) return score;

        oddeven = JsonUtils.toModel(json, Oddeven.class);
        if (oddeven == null) return score;

        // 봇에 결과가 있다면
        if (oddeven.isClosing()) {
            score.convertCard(oddeven.getCard1(), oddeven.getCard2());
            if (!"O".equals(oddeven.getOddeven()) && !"E".equals(oddeven.getOddeven())) {
                score.setCancel(true);
            }
        }
        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(OddevenDto.Score score) {
        Oddeven oddeven = oddevenRepository.findOne(score.getId());

        try {
            // 스코어 입력
            boolean success = score.convertCard();
            if (!success) {
                throw new ZoneRulesException("결과를 변환하지 못하였습니다.");
            }

            if (oddeven.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                oddevenGameService.rollbackPayment(oddeven);
            }

            oddeven.updateScore(score);

            oddevenRepository.saveAndFlush(oddeven);
            oddevenGameService.closingBetting(oddeven);
            oddevenBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("홀짝 {} - {}회차 수동처리를 하지 못하였습니다. - {}", oddeven.getGameDate(), oddeven.getRound(), e.getMessage());
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

        Iterable<Oddeven> iterable = oddevenRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(1))));
        for (Oddeven oddeven : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.ODDEVEN).and(qi.groupId.eq(oddeven.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getOddevenUrl() + "?sdate=" + oddeven.getSdate());
            if (json == null) continue;

            Oddeven result = JsonUtils.toModel(json, Oddeven.class);
            if (result == null) continue;

            if (result.isClosing()) {
                oddeven.setCard1(result.getCard1());
                oddeven.setCard2(result.getCard2());
                oddeven.setOddeven(result.getOddeven());
                oddeven.setClosing(true);
                oddevenRepository.saveAndFlush(oddeven);
                closing++;
            }
        }
        oddevenBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public OddevenDto.Config gameConfig() {
        OddevenDto.Config gameConfig = new OddevenDto.Config();
        OddevenConfig config = ZoneConfig.getOddeven();

        // 홀짝 바카라는 1분씩 댕겨 줘야 한다.
        Date gameDate = new Date(config.getZoneMaker().getGameDate().getTime() - 1000 * 60);
        Oddeven oddeven = oddevenRepository.findOne(q.gameDate.eq(gameDate));

        if (oddeven == null) {
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
        gameConfig.setGameDate(oddeven.getGameDate());
        gameConfig.setSdate(oddeven.getSdate());
        gameConfig.setRound(oddeven.getRound());
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setOdds(oddeven.getOdds());

        // 60초 보정
        int betTime = (int) (oddeven.getGameDate().getTime() - new Date().getTime() - config.getBetTime() * 1000 + 60000) / 1000;
        if (betTime < 0) betTime = 0;
        gameConfig.setBetTime(betTime);

        gameConfig.setOddeven(config.isOddeven());
        gameConfig.setOverunder(config.isOverunder());
        gameConfig.setPattern(config.isPattern());

        return gameConfig;
    }
}
