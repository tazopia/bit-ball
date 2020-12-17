package spoon.bot.zone.aladdin;

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
public class AladdinTask {

    private GameBotParsing aladdinParsing;

    @Scheduled(cron = "0 0/3 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        aladdinParsing.parsingGame();
    }

    @Scheduled(cron = "1/3 0/3 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        aladdinParsing.closingGame();
    }

    @Scheduled(cron = "1 1/3 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        aladdinParsing.checkResult();
    }

    @Scheduled(cron = "9 1 4 * * * ")
    public void deleteGame() {
        aladdinParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isAladdin() || !ZoneConfig.getAladdin().isEnabled();
    }

}
