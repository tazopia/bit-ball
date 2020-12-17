package spoon.bot.zone.baccarat;

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
public class BaccaratTask {

    private GameBotParsing baccaratParsing;

    @Scheduled(cron = "0 * * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        baccaratParsing.parsingGame();
    }

    @Scheduled(cron = "22/3 * * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        baccaratParsing.closingGame();
    }

    @Scheduled(cron = "1 * * * * *")
    public void checkResult() {
        if (notParsing()) return;
        baccaratParsing.checkResult();
    }

    @Scheduled(cron = "15 1 4 * * * ")
    public void deleteGame() {
        baccaratParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isBaccarat() || !ZoneConfig.getBaccarat().isEnabled();
    }
}
