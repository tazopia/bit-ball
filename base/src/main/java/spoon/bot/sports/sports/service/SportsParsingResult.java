package spoon.bot.sports.sports.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.sports.service.ParsingResult;
import spoon.bot.sports.sports.domain.BotSports;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;
import spoon.game.entity.Game;
import spoon.game.service.GameBotService;
import spoon.monitor.service.MonitorService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SportsParsingResult implements ParsingResult {

    private final GameBotService gameBotService;

    private final MonitorService monitorService;

    private static long udt;

    @Override
    @Async
    public void closingGame() {
        int closed = 0;

        String json = HttpParsing.getJson(getClosingUrl());
        if (StringUtils.empty(json)) return;

        List<BotSports> list = JsonUtils.toSportsBotList(json);

        for (BotSports bot : list) {
            if (udt < bot.getUdt()) udt = bot.getUdt();
            if (bot.getScoreHome() == null || bot.getScoreAway() == null) continue;

            Game game = gameBotService.getGame(bot.getSiteCode(), bot.getSiteId());
            if (game == null || game.isClosing() || game.isCancel()) continue;
            boolean success = gameBotService.gameScore(game.getId(), bot.getScoreHome(), bot.getScoreAway(), bot.isCancel(), bot.getUt());
            if (success) {
                closed++;
            }
        }

        monitorService.checkSports();

        log.debug("Sports 게임 클로징 - 전체 : {}, 클로징 : {}", list.size(), closed);
    }

    private String getClosingUrl() {
        switch (Config.getSysConfig().getSports().getSports()) {
            case "all":
                return Config.getSysConfig().getSports().getSportsApi() + "/api/v2/result?udt=" + udt;
            case "cross":
                return Config.getSysConfig().getSports().getSportsApi() + "/api/v2/cross/result?udt=" + udt;
            case "crossSoccer":
                return Config.getSysConfig().getSports().getSportsApi() + "/api/v2/crossSoccer/result?udt=" + udt;
            case "special":
                return Config.getSysConfig().getSports().getSportsApi() + "/api/v2/special/result?udt=" + udt;
            default:
                throw new IllegalArgumentException("SysConfig > Sports > sportsApi 의 정보가 잘못되었습니다. (" + Config.getSysConfig().getSports().getSports() + ")");
        }
    }
}
