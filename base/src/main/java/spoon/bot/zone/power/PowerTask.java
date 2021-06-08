package spoon.bot.zone.power;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.bot.zone.service.GameBotParsing;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneUtils;

@Slf4j
@AllArgsConstructor
@Component
public class PowerTask {

    private GameBotParsing powerParsing;

    @Scheduled(cron = "0 0/5 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        if (!ZoneUtils.enabledPower()) return;
        powerParsing.parsingGame();
    }

    @Scheduled(cron = "0/4 0/5 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        if (!ZoneUtils.enabledPower()) return;
        powerParsing.closingGame();
    }

    @Scheduled(cron = "1 4/5 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        powerParsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        powerParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isPower() || !ZoneConfig.getPower().isEnabled();
    }

}
