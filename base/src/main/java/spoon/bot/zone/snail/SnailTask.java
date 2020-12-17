package spoon.bot.zone.snail;

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
public class SnailTask {

    private GameBotParsing snailParsing;

    @Scheduled(cron = "0 0/5 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        snailParsing.parsingGame();
    }

    @Scheduled(cron = "13/3 0/5 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        snailParsing.closingGame();
    }

    @Scheduled(cron = "1 1/5 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        snailParsing.checkResult();
    }

    @Scheduled(cron = "3 1 4 * * * ")
    public void deleteGame() {
        snailParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isSnail() || !ZoneConfig.getSnail().isEnabled();
    }
}
