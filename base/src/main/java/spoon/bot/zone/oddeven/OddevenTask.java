package spoon.bot.zone.oddeven;

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
public class OddevenTask {

    private GameBotParsing oddevenParsing;

    @Scheduled(cron = "0 * * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        oddevenParsing.parsingGame();
    }

    @Scheduled(cron = "15/3 * * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        oddevenParsing.closingGame();
    }

    @Scheduled(cron = "1 * * * * *")
    public void checkResult() {
        if (notParsing()) return;
        oddevenParsing.checkResult();
    }

    @Scheduled(cron = "13 1 4 * * * ")
    public void deleteGame() {
        oddevenParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isOddeven() || !ZoneConfig.getOddeven().isEnabled();
    }
}
