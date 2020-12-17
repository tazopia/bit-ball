package spoon.bot.zone.dog;

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
public class DogTask {

    private GameBotParsing dogParsing;

    @Scheduled(fixedDelay = 30 * 1000, initialDelay = 15 * 1000)
    public void parsingGame() {
        if (notParsing()) return;
        dogParsing.parsingGame();
    }

    @Scheduled(fixedDelay = 14 * 1000, initialDelay = 25 * 1000)
    public void parsingResult() {
        if (notParsing()) return;
        dogParsing.closingGame();
    }

    @Scheduled(fixedDelay = 14 * 1000, initialDelay = 28 * 1000)
    public void checkResult() {
        if (notParsing()) return;
        dogParsing.checkResult();
    }

    @Scheduled(cron = "53 1 4 * * *")
    public void deleteGame() {
        dogParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isDog() || !ZoneConfig.getDog().isEnabled();
    }
}
