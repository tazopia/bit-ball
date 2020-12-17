package spoon.gameZone.dog.service;

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
import spoon.gameZone.dog.*;
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
public class DogServiceImpl implements DogService {

    private ConfigService configService;

    private MemberService memberService;

    private DogGameService dogGameService;

    private DogBotService dogBotService;

    private DogRepository dogRepository;

    private BetItemRepository betItemRepository;

    private static QDog q = QDog.dog;

    @Transactional
    @Override
    public boolean updateConfig(DogConfig dogConfig) {
        try {
            configService.updateZoneConfig("dog", JsonUtils.toString(dogConfig));
            ZoneConfig.setDog(dogConfig);
        } catch (RuntimeException e) {
            log.error("개경주 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public Iterable<Dog> getComplete() {
        return dogRepository.findAll(q.closing.isFalse(), new Sort(Sort.Direction.ASC, "sdate"));
    }

    @Override
    public Page<Dog> getClosing(ZoneDto.Command command, Pageable pageable) {
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

        return dogRepository.findAll(builder, pageable);
    }

    @Override
    public DogDto.Score findScore(Long id) {
        Dog dog = dogRepository.findOne(id);

        DogDto.Score score = new DogDto.Score();
        score.setId(dog.getId());
        score.setWinNumber(dog.getWinNumber());
        score.setCancel(dog.isCancel());
        score.setLeague(dog.getLeague());
        score.setGameDate(dog.getGameDate());

        // 봇 연결
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getDogUrl() + "/result/" + dog.getSdate());
        if (json == null) return score;

        dog = JsonUtils.toModel(json, Dog.class);
        if (dog == null) return score;

        // 봇에 결과가 있다면
        if (dog.isClosing()) {
            score.setWinNumber(dog.getWinNumber());
        }

        return score;
    }

    @Transactional
    @Override
    public boolean closingGame(DogDto.Score score) {
        Dog dog = dogRepository.findOne(score.getId());

        try {
            if (dog.isChangeResult(score)) {
                // 현재 지급된 머니 포인트를 되돌린다.
                dogGameService.rollbackPayment(dog);
            }

            // 스코어 입력
            dog.updateScore(score);
            dogRepository.saveAndFlush(dog);
            dogGameService.closingBetting(dog);
            dogBotService.checkResult();
        } catch (RuntimeException e) {
            log.error("개경주 {} - {}회차 수동처리를 하지 못하였습니다. - {}", dog.getGameDate(), dog.getSdate(), e.getMessage());
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

        Iterable<Dog> iterable = dogRepository.findAll(q.closing.isFalse().and(q.gameDate.before(DateUtils.beforeMinutes(1))));
        for (Dog dog : iterable) {
            total++;
            long count = betItemRepository.count(qi.menuCode.eq(MenuCode.DOG).and(qi.groupId.eq(dog.getSdate())).and(qi.cancel.isFalse()));
            if (count > 0) continue;

            String json = HttpParsing.getJson(Config.getSysConfig().getZone().getDogUrl() + "/result/" + dog.getSdate());
            if (json == null) continue;

            Dog result = JsonUtils.toModel(json, Dog.class);
            if (result == null) continue;

            if (result.isClosing()) {
                dog.setWinNumber(result.getWinNumber());
                dog.setClosing(true);
                dogRepository.saveAndFlush(dog);
                closing++;
            }
        }
        dogBotService.checkResult();
        return new AjaxResult(true, "전체 " + total + "경기중 " + closing + "경기를 종료처리 했습니다.");
    }

    @Override
    public DogDto.Config gameConfig() {
        DogDto.Config gameConfig = new DogDto.Config();
        DogConfig config = ZoneConfig.getDog();

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

        return gameConfig;
    }

    @Override
    public List<DogDto.List> gameList() {
        Iterable<Dog> list = dogRepository.findAll(q.gameDate.gt(new Date()), new Sort("gameDate"));
        return StreamSupport.stream(list.spliterator(), false).map(DogDto.List::new).collect(Collectors.toList());
    }

    @Override
    public List<DogDto.List> gameList(Long id) {
        String sdate = dogRepository.getGameSDateById(id);
        Iterable<Dog> list = dogRepository.findAll(q.sdate.gt(sdate), new Sort("gameDate"));
        return StreamSupport.stream(list.spliterator(), false).map(DogDto.List::new).collect(Collectors.toList());
    }
}
