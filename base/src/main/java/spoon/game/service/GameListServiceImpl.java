package spoon.game.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;
import spoon.game.domain.GameDto;
import spoon.game.domain.MenuCode;
import spoon.game.entity.Game;
import spoon.game.entity.QGame;
import spoon.game.repository.GameRepository;
import spoon.mapper.GameMapper;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class GameListServiceImpl implements GameListService {

    private GameRepository gameRepository;

    private GameMapper gameMapper;

    private static QGame q = QGame.game;

    @Override
    public List<GameDto.List> gameList(String menu, String sports) {
        // 스페셜은 스프레드가 1만 가능하다.
        int spread = "special".equals(menu) ? 10 : Config.getSysConfig().getSports().getSpread();

        List<GameDto.List> list = gameIngList(menu, sports, spread);

        // 종료 게임을 보여주기 원하면 보여 준다.
        if (Config.getGameConfig().isEndGame()) {
            list.addAll(gameEndList(menu, sports, spread));
        }

        return list;
    }

    private List<GameDto.List> gameEndList(String menu, String sports, int spread) {
        List<GameDto.List> list = gameMapper.gameEndList(menu, sports, spread);

        if (hasMore(menu)) {
            gameSortMore(list);
        } else {
            gameSort(list, true);
        }

        return list;
    }

    private List<GameDto.List> gameIngList(String menu, String sports, int spread) {
        List<GameDto.List> list = gameMapper.gameList(menu, sports, spread);

        if (hasMore(menu)) {
            gameSortMore(list);
        } else {
            gameSort(list, true);
        }

        return list;
    }

    /**
     * 메뉴 접기 펴기 사용 유무
     */
    private boolean hasMore(String menu) {
        switch (menu) {
            case "cross":
                return Config.getGameConfig().isMoreCross();
            case "special":
                return Config.getGameConfig().isMoreSpecial();
            case "live":
                return Config.getGameConfig().isMoreLive();
            default:
                return true;
        }
    }

    private void gameSortMore(List<GameDto.List> list) {
        gameSort(list, false);
        Collections.reverse(list);

        int show = 0;
        int cnt = 0;
        for (GameDto.List game : list) {
            cnt++;
            if (game.isShow()) show++;

            if (game.getSort() == 0) {
                if (show < cnt) {
                    game.setBtn(true);
                }
                game.setCount(cnt);
                cnt = 0;
                show = 0;
            }
        }

        Collections.reverse(list);
    }

    private void gameSort(List<GameDto.List> list, boolean show) {
        String league = "";
        Date gameDate = null;
        String groupId = "";
        String team = "";

        for (GameDto.List game : list) {
            if (!groupId.equals(game.getGroupId()) || ("365".equals(game.getSiteCode())) && !game.getTeamHome().equals(team)) {
                groupId = game.getGroupId();
                team = game.getTeamHome();
                game.setSort(1);
                game.setShow(true);
            }

            if (!league.equals(game.getLeagueName()) || !game.getGameDate().equals(gameDate)) {
                league = game.getLeagueName();
                gameDate = game.getGameDate();
                game.setSort(0);
                game.setShow(true);
            }

            if (show) game.setShow(true);
        }
    }

    @Override
    public List<GameDto.League> gameLeague(String menu, String sports) {
        int spread = "special".equals(menu) ? 10 : Config.getSysConfig().getSports().getSpread();
        List<GameDto.League> list = gameMapper.gameLeague(menu, sports, spread);
        String sport = "";
        for (GameDto.League league : list) {
            if (!sport.equals(league.getSports())) {
                league.setShow(true);
                sport = league.getSports();
            }
        }

        Collections.reverse(list);
        int total = 0;
        for (GameDto.League league : list) {
            total += league.getCnt();

            if (league.isShow()) {
                league.setTotal(total);
                total = 0;
            }
        }

        Collections.reverse(list);
        return list;
    }


    @Override
    public Page<Game> scoreList(MenuCode menuCode, String sports, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (menuCode == MenuCode.CROSS) {
            builder.and(q.menuCode.in(MenuCode.MATCH, MenuCode.HANDICAP));
        } else {
            builder.and(q.menuCode.eq(menuCode));
        }

        if (StringUtils.notEmpty(sports)) {
            builder.and(q.sports.eq(sports));
        }

        builder.and(q.closing.eq(true)).and(q.enabled.eq(true));

        Sort sort = new Sort(Sort.Direction.DESC, "gameDate").and(new Sort("sports", "league", "teamHome", "special", "sort"));
        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return gameRepository.findAll(builder, pageRequest);
    }


    @Override
    public Page<Game> readyGameList(GameDto.Command command, Pageable pageable) {
        Date last = DateUtils.beforeDays(-2);
        BooleanBuilder builder = buildMenuCodeAndSearch(command);
        builder.and(q.gameDate.gt(new Date()).and(q.gameDate.lt(last)).and(q.closing.isFalse()).and(q.enabled.isFalse()).and(q.deleted.isFalse()));
        return gameRepository.findAll(builder, getPageRequest(command.getSort(), pageable));
    }

    @Override
    public Page<Game> completeGameList(GameDto.Command command, Pageable pageable) {
        Date last = DateUtils.beforeDays(-2);
        BooleanBuilder builder = buildMenuCodeAndSearch(command);
        builder.and(q.gameDate.gt(new Date()).and(q.gameDate.lt(last)).and(q.closing.isFalse()).and(q.enabled.isTrue()).and(q.deleted.isFalse()));
        return gameRepository.findAll(builder, getPageRequest(command.getSort(), pageable));
    }

    @Override
    public Page<Game> closingGameList(GameDto.Command command, Pageable pageable) {
        BooleanBuilder builder = buildMenuCodeAndSearch(command);
        builder.and(q.closing.eq(true)).and(q.enabled.eq(true).or(q.amountTotal.gt(0)));
        return gameRepository.findAll(builder, getPageRequest(command.getSort(), pageable));
    }

    @Override
    public Page<Game> resultGameList(GameDto.Command command, Pageable pageable) {
        BooleanBuilder builder = buildMenuCodeAndSearch(command);
        builder.and(q.closing.eq(false)).and(q.enabled.eq(true).or(q.amountTotal.gt(0))).and(q.gameDate.lt(new Date()));
        return gameRepository.findAll(builder, getPageRequest(command.getSort(), pageable));
    }

    @Override
    public Page<Game> deletedGameList(GameDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getSports())) {
            builder.and(q.sports.eq(command.getSports()));
        }

        if (StringUtils.notEmpty(command.getLeague())) {
            builder.and(q.league.eq(command.getLeague()));
        }

        if (StringUtils.notEmpty(command.getTeam())) {
            builder.and(q.teamHome.like("%" + command.getTeam() + "%").or(q.teamAway.like("%" + command.getTeam() + "%")));
        }

        builder.and(q.deleted.isTrue()).and(q.gameDate.lt(DateUtils.beforeDays(-1)));
        return gameRepository.findAll(builder, getPageRequest(command.getSort(), pageable));
    }

    private BooleanBuilder buildMenuCodeAndSearch(GameDto.Command command) {
        BooleanBuilder builder = new BooleanBuilder();

        if (command.getMenuCode() == MenuCode.CROSS) { // 크로스
            builder.and(q.menuCode.in(MenuCode.MATCH, MenuCode.HANDICAP));
        } else if (MenuCode.isSports(command.getMenuCode())) { // 스포츠
            builder.and(q.menuCode.eq(command.getMenuCode()));
        }

        if (StringUtils.notEmpty(command.getSports())) {
            builder.and(q.sports.eq(command.getSports()));
        }

        if (StringUtils.notEmpty(command.getLeague())) {
            builder.and(q.league.eq(command.getLeague()));
        }

        if (StringUtils.notEmpty(command.getTeam())) {
            builder.and(q.teamHome.like("%" + command.getTeam() + "%").or(q.teamAway.like("%" + command.getTeam() + "%")));
        }

        return builder;
    }

    private PageRequest getPageRequest(String sortString, Pageable pageable) {
        Sort sort;
        if (StringUtils.empty(sortString)) {
            sort = new Sort("gameDate", "sports", "league", "teamHome", "special", "sort", "handicap");
        } else if ("date.desc".equals(sortString)) {
            sort = new Sort(Sort.Direction.DESC, "gameDate").and(new Sort("sports", "league", "teamHome", "special", "sort", "handicap"));
        } else if ("score.asc".equals(sortString)) {
            sort = new Sort(Sort.Direction.DESC, "scoreHome").and(new Sort("gameDate", "sports", "league", "teamHome", "special", "sort", "handicap"));
        } else {
            sort = new Sort(Sort.Direction.DESC, "amountTotal").and(new Sort("gameDate", "sports", "league", "teamHome", "special", "sort", "handicap"));
        }

        return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
