package spoon.bot.sports.ferrari.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import spoon.bot.sports.ferrari.domain.BotFerrari;
import spoon.bot.sports.file.UploadFlag;
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
import spoon.game.repository.LeagueRepository;
import spoon.game.service.GameBotService;
import spoon.game.service.sports.SportsService;
import spoon.game.service.sports.TeamService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class FerrariParsingGame implements ParsingGame {

    private GameBotService gameBotService;

    private SportsService sportsService;

    private TeamService teamService;

    private LeagueRepository leagueRepository;

    private ApplicationEventPublisher eventPublisher;

    private static long ms = 0;

    @Override
    public void parsingGame() {
        int saved = 0;
        int updated = 0;

        String json = HttpParsing.getJson(getParsingUrl());
        if (json == null) {
            return;
        }

        List<BotFerrari> list = JsonUtils.toFerrariList(json);

        for (BotFerrari bot : list) {
            if (ms < bot.getMs()) ms = bot.getMs();

            if (gameBotService.isExist(bot.getPv(), String.valueOf(bot.getSid()))) {
                if (updateGame(bot)) {
                    updated++;
                }
            } else {
                if (addGame(bot)) {
                    saved++;
                }
            }
        }
        log.debug("Ferrari 게임 업데이트 - 전체 : {}, 신규: {}, 업데이트: {}", list.size(), saved, updated);
    }

    @Override
    public void resetUt(String update) {
        ms = 0;
    }

    private boolean addGame(BotFerrari bot) {
        Optional<Game> game = makeGame(bot);
        if (game.isPresent()) {
            Game g = game.get();
            updateSports(g.getSportsBean());
            updateLeague(g.getLeagueBean(), bot.getFlag());
            updateTeam(g.getTeamHomeBean());
            updateTeam(g.getTeamAwayBean());
            return gameBotService.addGame(g);
        }
        return false;
    }

    private boolean updateGame(BotFerrari bot) {
        Game game = gameBotService.getGame(bot.getPv(), String.valueOf(bot.getSid()));
        // 자동 업데이트가 아니라면 업데이트를 하지 않는다.
        if (!game.isAutoUpdate()) return false;


        // 업데이트가 바뀌지 않으면 업데이트를 하지 않는다.
        if (String.valueOf(bot.getMs()).equals(game.getUt())) return false;

        copyGame(bot, game);
        updateSports(game.getSportsBean());
        //updateLeague(game.getLeagueBean(), bot.getFlag());
        updateTeam(game.getTeamHomeBean());
        updateTeam(game.getTeamAwayBean());

        return gameBotService.updateGame(game);
    }

    private Optional<Game> makeGame(BotFerrari bot) {
        try {
            Game game = new Game();
            game.setSiteCode(bot.getPv());
            game.setSiteId(String.valueOf(bot.getSid()));
            Date date = DateUtils.parse(bot.getSdate(), "yyyy-MM-dd HH:mm:ss");
            game.setGameDate(date == null ? DateUtils.beforeDays(5) : date);
            game.setSports(bot.getSports());
            game.setSpecial("");
            game.setMenuCode(MenuCode.valueOf(bot.getMenu().toUpperCase()));
            game.setGameCode(convertGameCode(bot.getType()));
            game.setLeague(bot.getLeague());
            game.setTeamHome(bot.getTeam1());
            game.setTeamAway(bot.getTeam2());
            if (game.getMenuCode() == MenuCode.MATCH) {
                game.setHandicap(0);
            } else {
                game.setHandicap(bot.getDraw());
            }
            game.setBetHome(true);
            game.setBetDraw(!"12".equals(bot.getType()));
            game.setBetAway(true);
            game.setDeleted("D".equals(bot.getState()));
            game.setCancel("C".equals(bot.getState()));
            game.setSort(convertSort(bot.getType()));
            game.setUt(String.valueOf(bot.getMs()));
            game.setAutoUpdate(autoUpdate());
            game.setEnabled(autoParsing());
            game.setClosing(false);
            game.setGroupId(gameBotService.findGroupId(game.getSiteId(), game.getSiteCode(), game.getGameDate(), game.getLeague(), game.getTeamHome(), game.getTeamAway()));
            makeOdds(bot, game);

            return Optional.of(game);
        } catch (RuntimeException e) {
            log.error("페라리 봇 게임으로 변경을 못하였습니다.", e);
        }

        return Optional.empty();
    }

    private double convertSort(String type) {
        switch (type) {
            case "12":
            case "1x2":
                return 100D;
            case "ah":
                return 200D;
            case "ou":
                return 300D;
            default:
                return 99999D;
        }
    }

    private GameCode convertGameCode(String type) {
        switch (type) {
            case "12":
            case "1x2":
                return GameCode.MATCH;
            case "ah":
                return GameCode.HANDICAP;
            case "ou":
                return GameCode.OVER_UNDER;
            default:
                throw new IllegalArgumentException("게임코드를 찾을 수 없습니다.");
        }
    }

    private void copyGame(BotFerrari bot, Game game) {
        Date date = DateUtils.parse(bot.getSdate(), "yyyy-MM-dd HH:mm:ss");
        game.setGameDate(date == null ? DateUtils.beforeDays(5) : date);
        game.setSports(bot.getSports());
        game.setSpecial("");
        game.setMenuCode(MenuCode.valueOf(bot.getMenu().toUpperCase()));
        game.setGameCode(convertGameCode(bot.getType()));
        game.setLeague(bot.getLeague());
        game.setTeamHome(bot.getTeam1());
        game.setTeamAway(bot.getTeam2());
        if (game.getMenuCode() == MenuCode.MATCH) {
            game.setHandicap(0);
        } else {
            game.setHandicap(bot.getDraw());
        }
        game.setBetHome(true);
        game.setBetDraw(!"12".equals(bot.getType()));
        game.setBetAway(true);
        game.setDeleted("D".equals(bot.getState()));
        game.setCancel("C".equals(bot.getState()));
        game.setSort(convertSort(bot.getType()));
        game.setUt(String.valueOf(bot.getMs()));
        game.setAutoUpdate(autoUpdate());
        game.setEnabled(autoParsing());

        makeOdds(bot, game);
    }

    private void makeOdds(BotFerrari bot, Game game) {
        double oddsUpDown = getOddsUpDown(game.getGameCode(), game.getMenuCode());
        double oddsPlus = getOddsPlus(game.getGameCode(), game.getMenuCode());
        double oddsDraw = 0D;

        if (oddsUpDown == 100D && oddsPlus == 0D) {
            game.updateOdds(bot.getHome(), bot.getDraw(), bot.getAway());
        } else {
            double oddsHome = convertOdds(bot.getHome(), oddsUpDown, oddsPlus);
            if ("1x2".equals(bot.getType())) {
                oddsDraw = convertOdds(bot.getDraw(), oddsUpDown, oddsPlus);
            }
            double oddsAway = convertOdds(bot.getAway(), oddsUpDown, oddsPlus);
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
        if (Config.getLeagueMap().containsKey(league.getKey())) return;
        UploadFlag upload = new UploadFlag(league.getSports(), league.getLeagueName(), Config.getSysConfig().getSports().getFerrariApi() + "/images/league/" + flag);
        eventPublisher.publishEvent(upload);
        leagueRepository.save(league);
        Config.getLeagueMap().put(league.getKey(), league);
    }

    private void updateSports(Sports sports) {
        if (sports.getId() == null || sports.getId() == 0) {
            sportsService.addSports(sports.getSportsName());
        }
    }

    private String getParsingUrl() {
        switch (Config.getSysConfig().getSports().getFerrari()) {
            case "all":
                return Config.getSysConfig().getSports().getFerrariApi() + "/api/v1/game?ms=" + ms;
            case "cross":
                return Config.getSysConfig().getSports().getFerrariApi() + "/api/v1/cross?ms=" + ms;
            case "special":
                return Config.getSysConfig().getSports().getFerrariApi() + "/api/v1/special?ms=" + ms;
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
