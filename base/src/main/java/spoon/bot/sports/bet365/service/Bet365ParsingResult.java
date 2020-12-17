package spoon.bot.sports.bet365.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.bot.sports.bet365.domain.BotBet365;
import spoon.bot.sports.service.ParsingResult;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.game.entity.Game;
import spoon.game.service.GameBotService;
import spoon.monitor.service.MonitorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class Bet365ParsingResult implements ParsingResult {

    private static String ut;
    private static int cnt = -1;
    private GameBotService gameBotService;
    private MonitorService monitorService;

    private static Map<Long, String> games = new HashMap<>();

    public void postConstruct() {
        gameBotService.initScore("avg").forEach(x -> games.put(x.getId(), x.getUt()));
    }

    @Override
    public void closingGame() {

        cnt++;
        if (cnt > 30) {
            cnt = 0;
            ut = null;
            games.clear();
            postConstruct();
        }

        String json = HttpParsing.getJson(getClosingUrl());
        if (json == null) {
            log.error("1xBet result is null");
            return;
        }

        List<BotBet365> list = JsonUtils.toBet365List(json);
        if (list == null) {
            log.error("1xBet Json is null");
            return;
        }

        for (BotBet365 bot : list) {
            if (ut == null || ut.compareTo(bot.getUt()) < 0) ut = bot.getUt();
            if (bot.getScoreHome() == null || bot.getScoreAway() == null) continue;

            String siteUt = games.get(Long.parseLong(bot.getSiteId()));
            if ((cnt == 0 && siteUt == null) || siteUt != null && siteUt.equals(bot.getUt())) continue;

            Game game = gameBotService.getGame(bot.getSiteCode(), bot.getSiteId());
            if (game == null || game.isClosing() || game.isCancel()) continue;
            gameBotService.gameScore(game.getId(), bot.getScoreHome(), bot.getScoreAway(), bot.isCancel(), bot.getUt());
        }

        monitorService.checkSports();

        //log.error("===================================================================== {}, {}", ut, cnt);
        log.info("1xBet 게임 클로징 - 전체 : {}, 클로징 : {}", list.size(), ut);

    }

    private String getClosingUrl() {
        switch (Config.getSysConfig().getSports().getBet365()) {
            case "all":
                return Config.getSysConfig().getSports().getBet365Api() + "/api/" + Config.getSysConfig().getSports().getBet365Pv() + "/result" + (ut == null ? "" : "?ut=" + WebUtils.encoding(ut));
            case "cross":
                return Config.getSysConfig().getSports().getBet365Api() + "/api/" + Config.getSysConfig().getSports().getBet365Pv() + "/cross/result" + (ut == null ? "" : "?ut=" + WebUtils.encoding(ut));
            case "crossSoccer":
                return Config.getSysConfig().getSports().getBet365Api() + "/api/" + Config.getSysConfig().getSports().getBet365Pv() + "/crossSoccer/result" + (ut == null ? "" : "?ut=" + WebUtils.encoding(ut));
            case "special":
                return Config.getSysConfig().getSports().getBet365Api() + "/api/" + Config.getSysConfig().getSports().getBet365Pv() + "/special/result" + (ut == null ? "" : "?ut=" + WebUtils.encoding(ut));
            default:
                throw new IllegalArgumentException("SysConfig > Sports > bet365Api 의 정보가 잘못되었습니다. (" + Config.getSysConfig().getSports().getBet365() + ")");
        }
    }
}
