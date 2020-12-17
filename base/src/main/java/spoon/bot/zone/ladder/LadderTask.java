package spoon.bot.zone.ladder;

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
public class LadderTask {

    private GameBotParsing ladderParsing;

    @Scheduled(cron = "0 0/5 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        ladderParsing.parsingGame();
    }

    @Scheduled(cron = "12/3 0/5 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        ladderParsing.closingGame();
    }

    @Scheduled(cron = "1 1/5 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        ladderParsing.checkResult();
    }

    @Scheduled(cron = "1 1 4 * * * ")
    public void deleteGame() {
        ladderParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isLadder() || !ZoneConfig.getLadder().isEnabled();
    }

}
