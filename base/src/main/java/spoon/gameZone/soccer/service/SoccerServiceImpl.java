package spoon.gameZone.soccer.service;

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
import spoon.gameZone.soccer.*;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Service
public class SoccerServiceImpl implements SoccerService {

    private ConfigService configService;

    private MemberService memberService;

    private SoccerGameService soccerGameService;

    private SoccerBotService soccerBotService;

    private SoccerRepository soccerRepository;

    private BetItemRepository betItemRepository;

    private static QSoccer q = QSoccer.soccer;

    @Transactional
    @Override
    public boolean updateConfig(SoccerConfig soccerConfig) {
        try {
            configService.updateZoneConfig("soccer", JsonUtils.toString(soccerConfig));
            ZoneConfig.setSoccer(soccerConfig);
        } catch (RuntimeException e) {
            log.error("가상축구 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Soccer> getComplete() {
        return soccerRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Soccer> getClosing(ZoneDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue());

        // 날짜별 검색
        if (StringUtils.notEmpty(command.getGameDate())) {
            builder.and(q.sdate.like(DateUtils.sdate(command.getGameDate())));
        }
        // 회차별 검색
        if (command.getRound() != null) {
            builder.and(q.sdate.like(String.valueOf(command.getRound()) + "%"));
        }

        // 리그별 검색
        if (StringUtils.notEmpty(command.getLeague())) {
            builder.and(q.league.eq(command.getLeague()));
        }

        return soccerRepository.findAll(builder, pageable);
    }

    @Override
    public SoccerDto.Score findScore(Long id) {
        Soccer soccer = soccerRepository.findOne(id);

        SoccerDto.Score score = new SoccerDto.Score();
        score.setId(soccer.getId());
        score.setScoreHome(soccer.getScoreHome());
        score.setScoreAway(soccer.getScoreAway());
        score.setCancel(soccer.isCancel());
        score.setTeamHome(soccer.getTeamHome());
        score.setTeamAway(soccer.getTeamAway());
        score.setLeague(soccer.getLeagueName());
        score.setGameDate(soccer.getGameDate());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getSoccerUrl() + "/result/" + soccer.getSdate());
        if (json == null) return score;

        soccer = JsonUtils.toModel(json, Soccer.class);
        if (soccer == null) return score;

        // 봇에 결과가 있다면
        if (soccer.isClosing()) {
            score.setScoreHome(soccer.getScoreHome());
            score.setScoreAway(soccer.getScoreAway());
            score.setClosing(true);
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(SoccerDto.Score score) {
        Soccer soccer = soccerRepository.findOne(score.getId());

        try {
            if (soccer.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                soccerGameService.rollbackPayment(soccer);
            }

            // 스코어 입력
            soccer.updateScore(score);
            soccerRepository.saveAndFlush(soccer);
            soccerGameService.closingBetting(soccer);
            soccerBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("가상축구 {} - {}회차 수동처리를 하지 못하였습니다. - {}", soccer.getGameDate(), soccer.getSdate(), e.getMessage());
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

        Iterable<Soccer> iterable = soccerRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(1))));
        for (Soccer soccer : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.SOCCER).and(qi.groupId.eq(soccer.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getSoccerUrl() + "/result/" + soccer.getSdate());
            if (json == null) continue;

            Soccer result = JsonUtils.toModel(json, Soccer.class);
            if (result == null) continue;

            if (result.isClosing()) {
                soccer.setScoreHome(result.getScoreHome());
                soccer.setScoreAway(result.getScoreAway());
                soccer.setClosing(true);
                soccerRepository.saveAndFlush(soccer);
                closing++;
            }
        }
        soccerBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public SoccerDto.Config gameConfig() {
        SoccerDto.Config gameConfig = new SoccerDto.Config();
        SoccerConfig config = ZoneConfig.getSoccer();

        String userid = WebUtils.userid();
        if (userid == null) return gameConfig;

        Member member = memberService.getMember(userid);
        int level = member.getLevel();
        gameConfig.setEnabled(config.isEnabled() && Config.getSysConfig().getZone().isSoccer());
        if (member.getRole() == Role.DUMMY) {
            gameConfig.setMoney(10000000);
        } else {
            gameConfig.setMoney(member.getMoney());
        }
        gameConfig.setWin(config.getWin()[level]);
        gameConfig.setMax(config.getMax()[level]);
        gameConfig.setMin(config.getMin()[level]);
        gameConfig.setMa(config.isMa());
        gameConfig.setAh(config.isAh());
        gameConfig.setOu(config.isOu());

        return gameConfig;
    }

    @Override
    public List<SoccerDto.List> gameList() {
        Iterable<Soccer> list = soccerRepository.findAll(q.gameDate.gt(new Date()), new Sort("gameDate"));
        return StreamSupport.stream(list.spliterator(), false).map(SoccerDto.List::new).collect(Collectors.toList());
    }

    @Override
    public List<SoccerDto.List> gameList(Long id) {
        String sdate = soccerRepository.getGameSDateById(id);
        Iterable<Soccer> list = soccerRepository.findAll(q.sdate.gt(sdate), new Sort("gameDate"));
        return StreamSupport.stream(list.spliterator(), false).map(SoccerDto.List::new).collect(Collectors.toList());
    }
}
