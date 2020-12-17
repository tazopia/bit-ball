package spoon.bot.zone.crownBaccarat;

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
public class CrownBaccaratTask {

    private GameBotParsing crownBaccaratParsing;

    @Scheduled(cron = "0 * * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        crownBaccaratParsing.parsingGame();
    }

    @Scheduled(cron = "5/3 * * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        crownBaccaratParsing.closingGame();
    }

    @Scheduled(cron = "1 * * * * *")
    public void checkResult() {
        if (notParsing()) return;
        crownBaccaratParsing.checkResult();
    }

    @Scheduled(cron = "13 1 4 * * * ")
    public void deleteGame() {
        crownBaccaratParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isCrownBaccarat() || !ZoneConfig.getCrownBaccarat().isEnabled();
    }
}
