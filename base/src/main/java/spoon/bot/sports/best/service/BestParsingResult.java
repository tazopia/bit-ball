package spoon.bot.sports.best.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.sports.best.domain.BotBest;
import spoon.bot.sports.service.ParsingResult;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.game.entity.Game;
import spoon.game.service.GameBotService;
import spoon.monitor.service.MonitorService;

import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class BestParsingResult implements ParsingResult {

    private GameBotService gameBotService;

    private MonitorService monitorService;

    private static String ut;

    private static boolean action = false;

    @Override
    @Async
    public void closingGame() {
        if (action) return;

        action = true;
        int closed = 0;

        String json = HttpParsing.getJson(getClosingUrl());
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
            if (bot.getScoreHome() == null || bot.getScoreAway() == null) continue;

            Game game = gameBotService.getGame(bot.getSiteCode(), String.valueOf(bot.getId()));
            if (game == null || game.isClosing() || game.isCancel()) continue;
            boolean success = gameBotService.gameScore(game.getId(), bot.getScoreHome(), bot.getScoreAway(), bot.isCancel(), DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            if (success) {
                closed++;
            }
        }

        monitorService.checkSports();

        log.debug("Best 게임 클로징 - 전체 : {}, 클로징 : {}", list.size(), closed);
        action = false;
    }

    private String getClosingUrl() {
        if (ut == null) ut = gameBotService.getMaxUt("best", true);
        switch (Config.getSysConfig().getSports().getBest()) {
            case "all":
                return Config.getSysConfig().getSports().getBestApi() + "/api/result" + (ut == null ? "" : "?ut=" + WebUtils.encoding(ut));
            case "cross":
                return Config.getSysConfig().getSports().getBestApi() + "/api/result/cross" + (ut == null ? "" : "?ut=" + WebUtils.encoding(ut));
            case "special":
                return Config.getSysConfig().getSports().getBestApi() + "/api/result/special" + (ut == null ? "" : "?ut=" + WebUtils.encoding(ut));
            default:
                throw new IllegalArgumentException("SysConfig > Sports > BestApi 의 정보가 잘못되었습니다. (" + Config.getSysConfig().getSports().getBest() + ")");
        }
    }
}
