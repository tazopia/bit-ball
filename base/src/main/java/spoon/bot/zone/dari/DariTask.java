package spoon.bot.zone.dari;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.bot.zone.service.GameBotParsing;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;

@Slf4j
@AllArgsConstructor
@Component
public class DariTask {

    private GameBotParsing dariParsing;

    @Scheduled(cron = "0 0/3 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        dariParsing.parsingGame();
    }

    @Scheduled(cron = "11/3 0/3 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        dariParsing.closingGame();
    }

    @Scheduled(cron = "1 1/3 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        dariParsing.checkResult();
    }

    @Scheduled(cron = "5 1 4 * * * ")
    public void deleteGame() {
        dariParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isDari() || !ZoneConfig.getDari().isEnabled();
    }

}
