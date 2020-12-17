package spoon.bot.zone.lowhi;

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
public class LowhiTask {

    private GameBotParsing lowhiParsing;

    @Scheduled(cron = "0 0/3 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        lowhiParsing.parsingGame();
    }

    @Scheduled(cron = "2/3 0/3 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        lowhiParsing.closingGame();
    }

    @Scheduled(cron = "1 1/3 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        lowhiParsing.checkResult();
    }

    @Scheduled(cron = "11 1 4 * * * ")
    public void deleteGame() {
        lowhiParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isLowhi() || !ZoneConfig.getLowhi().isEnabled();
    }
}
