package spoon.bot.zone.kenoLadder;

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
public class KenoLadderTask {

    private GameBotParsing kenoLadderParsing;

    @Scheduled(cron = "0 0/5 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        kenoLadderParsing.parsingGame();
    }

    @Scheduled(cron = "1/3 0/5 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        kenoLadderParsing.closingGame();
    }

    @Scheduled(cron = "1 1/5 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        kenoLadderParsing.checkResult();
    }

    @Scheduled(cron = "1 3 4 * * * ")
    public void deleteGame() {
        kenoLadderParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isKenoLadder() || !ZoneConfig.getKenoLadder().isEnabled();
    }

}
