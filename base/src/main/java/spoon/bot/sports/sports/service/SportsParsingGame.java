package spoon.bot.sports.sports.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.sports.file.UploadFlag;
import spoon.bot.sports.service.ParsingGame;
import spoon.bot.sports.sports.domain.BotSports;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SportsParsingGame implements ParsingGame {

    private final GameBotService gameBotService;

    private final SportsService sportsService;

    private final LeagueRepository leagueRepository;

    private final TeamService teamService;

    private final ApplicationEventPublisher eventPublisher;

    private static long udt;

    @Override
    @Async
    public void parsingGame() {
        int saved = 0;
        int updated = 0;

        String json = HttpParsing.getJson(getParsingUrl());
        if (StringUtils.empty(json)) return;

        List<BotSports> list = JsonUtils.toSportsBotList(json);

        for (BotSports bot : list) {
            if (udt < bot.getUdt()) udt = bot.getUdt();

            if (gameBotService.isExist(bot.getSiteCode(), bot.getSiteId())) {
                if (updateGame(bot)) {
                    updated++;
                }
            } else {
                if (addGame(bot)) {
                    saved++;
                }
            }
        }
        log.debug("Sports 게임 업데이트 - 전체 : {}, 신규: {}, 업데이트: {}", list.size(), saved, updated);
    }

    @Override
    public void resetUt(String update) {
        udt = 0;
    }

    private boolean addGame(BotSports bot) {
        Game game = makeGame(bot);
        updateSports(game.getSportsBean());
        updateLeague(game.getLeagueBean(), bot.getLeagueFlag());
        updateTeam(game.getTeamHomeBean());
        updateTeam(game.getTeamAwayBean());

        return gameBotService.addGame(game);
    }

    private boolean updateGame(BotSports bot) {
        Game game = gameBotService.getGame(bot.getSiteCode(), bot.getSiteId());
        // 자동 업데이트가 아니라면 업데이트를 하지 않는다.
        if (!game.isAutoUpdate()) return false;

        // 업데이트가 바뀌지 않으면 업데이트를 하지 않는다.
        if (bot.getUdt() == Optional.ofNullable(game.getUdt()).orElse(0L)) return false;

        copyGame(bot, game);

        updateSports(game.getSportsBean());
        updateLeague(game.getLeagueBean(), bot.getLeagueFlag());
        updateTeam(game.getTeamHomeBean());
        updateTeam(game.getTeamAwayBean());

        return gameBotService.updateGame(game);
    }

    private Game makeGame(BotSports bot) {
        Game game = new Game();
        game.setSiteCode(bot.getSiteCode());
        game.setSiteId(bot.getSiteId());
        game.setGroupId(bot.getGroupId());
        game.setGameDate(bot.getGameDate());
        game.setSports(bot.getSports());
        game.setMenuCode(bot.getMenuCode());
        game.setGameCode(bot.getGameCode());
        game.setSpecial(bot.getSpecial());
        game.setLeague(bot.getLeague());
        game.setTeamHome(bot.getTeamHome());
        game.setTeamAway(bot.getTeamAway());
        game.setHandicap(bot.getHandicap());
        game.setBetHome(bot.isBetHome());
        game.setBetDraw(bot.isBetDraw());
        game.setBetAway(bot.isBetAway());
        game.setDeleted(bot.isDeleted());
        game.setClosing(bot.isClosing());
        game.setCancel(bot.isCancel());
        game.setSort(bot.getSort());
        game.setUt(bot.getUt());
        game.setAutoUpdate(autoUpdate());
        game.setEnabled(bot.isEnabled());
        game.setUdt(bot.getUdt());
        makeOdds(bot, game);

        return game;
    }

    private void copyGame(BotSports bot, Game game) {
        game.setGameDate(bot.getGameDate());
        game.setSports(bot.getSports());
        game.setMenuCode(bot.getMenuCode());
        game.setGameCode(bot.getGameCode());
        game.setSpecial(bot.getSpecial());
        game.setLeague(bot.getLeague());
        game.setTeamHome(bot.getTeamHome());
        game.setTeamAway(bot.getTeamAway());
        game.setHandicap(bot.getHandicap());
        game.setBetHome(bot.isBetHome());
        game.setBetDraw(bot.isBetDraw());
        game.setBetAway(bot.isBetAway());
        game.setDeleted(bot.isDeleted());
        game.setClosing(bot.isClosing());
        game.setCancel(bot.isCancel());
        game.setEnabled(bot.isEnabled());
        game.setSort(bot.getSort());
        game.setUt(bot.getUt());
        game.setUdt(bot.getUdt());
        makeOdds(bot, game);
    }

    private void makeOdds(BotSports bot, Game game) {
        double oddsUpDown = getOddsUpDown(bot.getGameCode(), bot.getMenuCode());
        double oddsPlus = getOddsPlus(bot.getGameCode(), bot.getMenuCode());
        double oddsDraw = 0D;

        if (oddsUpDown == 100D && oddsPlus == 0D) {
            game.updateOdds(bot.getOddsHome(), bot.getOddsDraw(), bot.getOddsAway());
        } else {
            double oddsHome = convertOdds(bot.getOddsHome(), oddsUpDown, oddsPlus);
            // 승무패 무가 없는 경우
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
        if (Config.getLeagueMap().containsKey(league.getKey())) return;
        UploadFlag upload = new UploadFlag(league.getSports(), league.getLeagueName(), Config.getSysConfig().getSports().getSportsApi() + "/images/league/" + flag);
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
        switch (Config.getSysConfig().getSports().getSports()) {
            case "all":
                return Config.getSysConfig().getSports().getSportsApi() + "/api/v2/list?udt=" + udt;
            case "cross":
                return Config.getSysConfig().getSports().getSportsApi() + "/api/v2/cross/list?udt=" + udt;
            case "crossSoccer":
                return Config.getSysConfig().getSports().getSportsApi() + "/api/v2/crossSoccer/list?udt=" + udt;
            case "special":
                return Config.getSysConfig().getSports().getSportsApi() + "/api/v2/special/list?udt=" + udt;
            default:
                throw new IllegalArgumentException("SysConfig > Sports > sportsApi 의 정보가 잘못되었습니다. (" + Config.getSysConfig().getSports().getSports() + ")");
        }
    }

    private boolean autoParsing() {
        return Config.getGameConfig().isAutoParsing();
    }

    private boolean autoUpdate() {
        return Config.getGameConfig().isAutoUpdate();
    }
}
