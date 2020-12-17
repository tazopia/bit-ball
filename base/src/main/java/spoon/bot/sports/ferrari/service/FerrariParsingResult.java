package spoon.bot.sports.ferrari.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.bot.sports.ferrari.domain.BotFerrari;
import spoon.bot.sports.service.ParsingResult;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.game.entity.Game;
import spoon.game.service.GameBotService;
import spoon.monitor.service.MonitorService;

import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class FerrariParsingResult implements ParsingResult {

    private GameBotService gameBotService;

    private MonitorService monitorService;

    private static long ms;

    @Override
    public void closingGame() {
        int closed = 0;

        String json = HttpParsing.getJson(getClosingUrl());
        if (json == null) {
            return;
        }

        List<BotFerrari> list = JsonUtils.toFerrariList(json);

        for (BotFerrari bot : list) {
            if (ms < bot.getMs()) ms = bot.getMs();
            Game game = gameBotService.getGame(bot.getPv(), String.valueOf(bot.getSid()));
            if (game == null || game.isClosing() || game.isCancel()) continue;
            boolean success = gameBotService.gameScore(game.getId(), bot.getScore1(), bot.getScore2(), "C".equals(bot.getState()), DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            if (success) {
                closed++;
            }
        }

        monitorService.checkSports();

        log.debug("Best 게임 클로징 - 전체 : {}, 클로징 : {}", list.size(), closed);
    }

    private String getClosingUrl() {
        return Config.getSysConfig().getSports().getFerrariApi() + "/api/v1/score?ms=" + ms;
    }
}
