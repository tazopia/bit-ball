package spoon.bot.zone.power;

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
public class PowerTask {

    private GameBotParsing powerParsing;

    @Scheduled(cron = "55 2/5 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        powerParsing.parsingGame();
    }

    @Scheduled(cron = "0/4 3/5 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
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
