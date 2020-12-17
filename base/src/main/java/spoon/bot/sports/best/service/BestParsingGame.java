package spoon.bot.sports.best.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.sports.best.domain.BotBest;
import spoon.bot.sports.service.ParsingGame;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.game.domain.GameCode;
import spoon.game.domain.MenuCode;
import spoon.game.entity.Game;
import spoon.game.entity.League;
import spoon.game.entity.Sports;
import spoon.game.entity.Team;
import spoon.game.service.GameBotService;
import spoon.game.service.sports.LeagueService;
import spoon.game.service.sports.SportsService;
import spoon.game.service.sports.TeamService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class BestParsingGame implements ParsingGame {

    private GameBotService gameBotService;

    private SportsService sportsService;

    private LeagueService leagueService;

    private TeamService teamService;

    private static String ut;

    private static boolean action = false;

    @Override
    @Async
    public void parsingGame() {
        if (action) return;

        action = true;
        int saved = 0;
        int updated = 0;

        String json = HttpParsing.getJson(getParsingUrl());
        if (json == null) {
            action = false;
            return;
        }

        List<BotBest> list = JsonUtils.toBestList(json);
        if (list == null) {
            action = false;
            return;
        }

        for (BotBest bot : list) {
            if (ut == null || ut.compareTo(String.valueOf(bot.getUt())) < 0) ut = String.valueOf(bot.getUt());

            if (gameBotService.isExist(bot.getSiteCode(), String.valueOf(bot.getId()))) {
                if (updateGame(bot)) {
                    updated++;
                }
            } else {
                if (addGame(bot)) {
                    saved++;
                }
            }
        }
        log.debug("Best 게임 업데이트 - 전체 : {}, 신규: {}, 업데이트: {}", list.size(), saved, updated);
        action = false;
    }

    @Override
    public void resetUt(String update) {
        ut = update;
    }

    private boolean addGame(BotBest bot) {
        Game game = makeGame(bot);
        updateSports(game.getSportsBean());
        updateLeague(game.getLeagueBean(), bot.getFlag());
        updateTeam(game.getTeamHomeBean());
        updateTeam(game.getTeamAwayBean());

        return gameBotService.addGame(game);
    }

    private boolean updateGame(BotBest bot) {
        Game game = gameBotService.getGame(bot.getSiteCode(), String.valueOf(bot.getId()));
        // 자동 업데이트가 아니라면 업데이트를 하지 않는다.
        if (!game.isAutoUpdate()) return false;


        // 업데이트가 바뀌지 않으면 업데이트를 하지 않는다.
        if (String.valueOf(bot.getUt()).equals(game.getUt())) return false;

        copyGame(bot, game);
        updateSports(game.getSportsBean());
        updateLeague(game.getLeagueBean(), bot.getFlag());
        updateTeam(game.getTeamHomeBean());
        updateTeam(game.getTeamAwayBean());

        return gameBotService.updateGame(game);
    }

    private Game makeGame(BotBest bot) {
        Game game = new Game();
        game.setSiteCode(bot.getSiteCode());
        game.setSiteId(String.valueOf(bot.getId()));
        Date date = DateUtils.parse(bot.getGameDate().trim(), "yyyy-MM-dd HH:mm");
        game.setGameDate(date == null ? DateUtils.beforeDays(5) : date);
        game.setSports(bot.getSports());
        if ("Y".equals(bot.getSpecial())) {
            game.setMenuCode(MenuCode.SPECIAL);
            game.setSpecial(BestSupport.getSpecial(bot.getTeamHome()));
        } else {
            game.setMenuCode("0".equals(bot.getGameCode()) ? MenuCode.MATCH : MenuCode.HANDICAP);
            game.setSpecial("");
        }
        game.setGameCode("0".equals(bot.getGameCode()) ? GameCode.MATCH : ("1".equals(bot.getGameCode()) ? GameCode.HANDICAP : GameCode.OVER_UNDER));
        game.setLeague(bot.getLeague());
        game.setTeamHome(bot.getTeamHome());
        game.setTeamAway(bot.getTeamAway());
        game.setHandicap(bot.getHandicap());
        game.setBetHome(true);
        game.setBetDraw(bot.getOddsDraw() > 0);
        game.setBetAway(true);
        game.setDeleted(false);
        game.setCancel(bot.isCancel());
        game.setSort("0".equals(bot.getGameCode()) ? 100 : ("1".equals(bot.getGameCode()) ? 200 : 300));
        game.setUt(String.valueOf(bot.getUt()));
        game.setAutoUpdate(autoUpdate());
        game.setEnabled(autoParsing());
        game.setClosing(false);
        game.setGroupId(gameBotService.findGroupId(game.getSiteId(), game.getSiteCode(), game.getGameDate(), game.getLeague(), game.getTeamHome(), game.getTeamAway()));
        makeOdds(bot, game);

        return game;
    }

    private void copyGame(BotBest bot, Game game) {
        Date date = DateUtils.parse(bot.getGameDate().trim(), "yyyy-MM-dd HH:mm");
        game.setGameDate(date == null ? DateUtils.beforeDays(5) : date);
        game.setSports(bot.getSports());
        if ("Y".equals(bot.getSpecial())) {
            game.setMenuCode(MenuCode.SPECIAL);
            game.setSpecial(BestSupport.getSpecial(bot.getTeamHome()));
        } else {
            game.setMenuCode("0".equals(bot.getGameCode()) ? MenuCode.MATCH : MenuCode.HANDICAP);
            game.setSpecial("");
        }
        game.setGameCode("0".equals(bot.getGameCode()) ? GameCode.MATCH : ("1".equals(bot.getGameCode()) ? GameCode.HANDICAP : GameCode.OVER_UNDER));
        game.setSpecial(bot.getSpecial());
        game.setLeague(bot.getLeague());
        game.setTeamHome(bot.getTeamHome());
        game.setTeamAway(bot.getTeamAway());
        game.setHandicap(bot.getHandicap());
        game.setBetHome(true);
        game.setBetDraw(bot.getOddsDraw() > 0);
        game.setBetAway(true);
        game.setCancel(bot.isCancel());
        game.setSort("0".equals(bot.getGameCode()) ? 100 : ("1".equals(bot.getGameCode()) ? 200 : 300));
        game.setUt(String.valueOf(bot.getUt()));
        makeOdds(bot, game);
    }

    private void makeOdds(BotBest bot, Game game) {
        double oddsUpDown = getOddsUpDown(game.getGameCode(), game.getMenuCode());
        double oddsPlus = getOddsPlus(game.getGameCode(), game.getMenuCode());
        double oddsDraw = 0D;

        if (oddsUpDown == 100D && oddsPlus == 0D) {
            game.updateOdds(bot.getOddsHome(), bot.getOddsDraw(), bot.getOddsAway());
        } else {
            double oddsHome = convertOdds(bot.getOddsHome(), oddsUpDown, oddsPlus);
            if (bot.getOddsDraw() != 0) {
                oddsDraw = convertOdds(bot.getOddsDraw(), oddsUpDown, oddsPlus);
            }
            double oddsAway = convertOdds(bot.getOddsAway(), oddsUpDown, oddsPlus);
            game.updateOdds(oddsHome, oddsDraw, oddsAway);
        }
    }

    private double getOddsUpDown(GameCode gameCode, MenuCode menuCode) {
        double odds = 100D;
        switch (gameCode) {
            case MATCH:
                odds = Config.getGameConfig().getOddsUpDownMatch();
                break;
            case HANDICAP:
                odds = menuCode == MenuCode.SPECIAL ? Config.getGameConfig().getOddsUpDownHandicapSpecial() : Config.getGameConfig().getOddsUpDownHandicap();
                break;
            case OVER_UNDER:
                odds = menuCode == MenuCode.SPECIAL ? Config.getGameConfig().getOddsUpDownOverUnderSpecial() : Config.getGameConfig().getOddsUpDownOverUnder();
                break;
        }
        return odds;
    }

    private double getOddsPlus(GameCode gameCode, MenuCode menuCode) {
        double odds = 0D;
        switch (gameCode) {
            case MATCH:
                odds = Config.getGameConfig().getOddsPlusMatch();
                break;
            case HANDICAP:
                odds = menuCode == MenuCode.SPECIAL ? Config.getGameConfig().getOddsPlusHandicapSpecial() : Config.getGameConfig().getOddsPlusHandicap();
                break;
            case OVER_UNDER:
                odds = menuCode == MenuCode.SPECIAL ? Config.getGameConfig().getOddsPlusOverUnderSpecial() : Config.getGameConfig().getOddsPlusOverUnder();
                break;
        }
        return odds;
    }

    private double convertOdds(double odds, double oddsRate, double oddsPlusRate) {
        if (oddsRate != 100D) {
            odds = BigDecimal.valueOf(odds)
                    .multiply(BigDecimal.valueOf(oddsRate))
                    .divide(BigDecimal.valueOf(100D), 2, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
        }
        if (oddsPlusRate != 0D) {
            odds = BigDecimal.valueOf(odds).add(BigDecimal.valueOf(oddsPlusRate)).doubleValue();
        }
        return odds;
    }

    private void updateTeam(Team team) {
        if (team.getId() == null || team.getId() == 0) {
            teamService.addTeam(team);
        }
    }

    private void updateLeague(League league, String flag) {
        if (league.getId() == null || league.getId() == 0) {
            leagueService.addLeague(league, flag);
        } else if ("league.png".equals(league.getLeagueFlag())) {
            leagueService.updateFlag(league, flag);
        }
    }

    private void updateSports(Sports sports) {
        if (sports.getId() == null || sports.getId() == 0) {
            sportsService.addSports(sports.getSportsName());
        }
    }

    private String getParsingUrl() {
        if (ut == null) ut = gameBotService.getMaxUt("Best", false);
        switch (Config.getSysConfig().getSports().getBest()) {
            case "all":
                return Config.getSysConfig().getSports().getBestApi() + "/api/list" + (ut == null ? "" : "?ut=" + ut);
            case "cross":
                return Config.getSysConfig().getSports().getBestApi() + "/api/list/cross" + (ut == null ? "" : "?ut=" + ut);
            case "special":
                return Config.getSysConfig().getSports().getBestApi() + "/api/list/special" + (ut == null ? "" : "?ut=" + ut);
            default:
                throw new IllegalArgumentException("SysConfig > Sports > BestApi 의 정보가 잘못되었습니다. (" + Config.getSysConfig().getSports().getBest() + ")");
        }
    }

    private boolean autoParsing() {
        return Config.getGameConfig().isAutoParsing();
    }

    private boolean autoUpdate() {
        return Config.getGameConfig().isAutoUpdate();
    }
}
