package spoon.bot.zone.soccer;

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
public class SoccerTask {

    private GameBotParsing soccerParsing;

    @Scheduled(fixedDelay = 30 * 1000, initialDelay = 10 * 1000)
    public void parsingGame() {
        if (notParsing()) return;
        soccerParsing.parsingGame();
    }

    @Scheduled(fixedDelay = 14 * 1000, initialDelay = 20 * 1000)
    public void parsingResult() {
        if (notParsing()) return;
        soccerParsing.closingGame();
    }

    @Scheduled(fixedDelay = 14 * 1000, initialDelay = 23 * 1000)
    public void checkResult() {
        if (notParsing()) return;
        soccerParsing.checkResult();
    }

    @Scheduled(cron = "51 1 4 * * *")
    public void deleteGame() {
        soccerParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isSoccer() || !ZoneConfig.getSoccer().isEnabled();
    }
}
