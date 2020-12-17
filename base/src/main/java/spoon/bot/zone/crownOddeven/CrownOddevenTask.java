package spoon.bot.zone.crownOddeven;

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
public class CrownOddevenTask {

    private GameBotParsing crownOddevenParsing;

    @Scheduled(cron = "0 * * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        crownOddevenParsing.parsingGame();
    }

    @Scheduled(cron = "5/3 * * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        crownOddevenParsing.closingGame();
    }

    @Scheduled(cron = "1 * * * * *")
    public void checkResult() {
        if (notParsing()) return;
        crownOddevenParsing.checkResult();
    }

    @Scheduled(cron = "13 1 4 * * * ")
    public void deleteGame() {
        crownOddevenParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isCrownOddeven() || !ZoneConfig.getCrownOddeven().isEnabled();
    }
}
