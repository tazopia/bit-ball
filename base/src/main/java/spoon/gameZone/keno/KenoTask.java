package spoon.gameZone.keno;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.keno.bot.KenoParsing;

@Slf4j
@RequiredArgsConstructor
@Component
public class KenoTask {

    private final KenoParsing kenoParsing;

    @Scheduled(cron = "0 0/5 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        kenoParsing.parsingGame();
    }

    @Scheduled(cron = "1/3 0/5 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        kenoParsing.closingGame();
    }

    @Scheduled(cron = "1 1/5 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        kenoParsing.checkResult();
    }

    @Scheduled(cron = "1 3 4 * * * ")
    public void deleteGame() {
        kenoParsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isKeno() || !ZoneConfig.getKeno().isEnabled();
    }

}
