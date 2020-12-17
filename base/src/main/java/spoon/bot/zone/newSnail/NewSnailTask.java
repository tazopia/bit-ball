package spoon.bot.zone.newSnail;

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
public class NewSnailTask {

    private GameBotParsing newSnailParsing;

    @Scheduled(cron = "55 2/3 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        newSnailParsing.parsingGame();
    }

    @Scheduled(cron = "3/3 0/3 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        newSnailParsing.closingGame();
    }

    @Scheduled(cron = "3 1/3 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        newSnailParsing.checkResult();
    }

    @Scheduled(cron = "3 11 4 * * * ")
    public void deleteGame() {
        newSnailParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isNewSnail() || !ZoneConfig.getNewSnail().isEnabled();
    }
}
