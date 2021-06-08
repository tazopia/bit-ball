package spoon.bot.zone.PowerLadder;

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
public class PowerLadderTask {

    private GameBotParsing powerLadderParsing;

    @Scheduled(cron = "0 0/5 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        powerLadderParsing.parsingGame();
    }

    @Scheduled(cron = "0/4 0/5 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        powerLadderParsing.closingGame();
    }

    @Scheduled(cron = "1 4/5 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        powerLadderParsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        powerLadderParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isPowerLadder() || !ZoneConfig.getPowerLadder().isEnabled();
    }

}
