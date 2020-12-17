package spoon.bot.zone.luck;

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
public class LuckTask {

    private GameBotParsing luckParsing;

    @Scheduled(cron = "0 0/2 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        luckParsing.parsingGame();
    }

    @Scheduled(cron = "1/3 0/2 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        luckParsing.closingGame();
    }

    @Scheduled(cron = "1 1/2 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        luckParsing.checkResult();
    }

    @Scheduled(cron = "53 1 4 * * * ")
    public void deleteGame() {
        luckParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isLuck() || !ZoneConfig.getLuck().isEnabled();
    }

}
