package spoon.gameZone.eos5;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos5.bot.Eos5Parsing;

@Slf4j
@RequiredArgsConstructor
@Component
public class Eos5Task {

    private final Eos5Parsing eos5Parsing;

    @Scheduled(cron = "0 0/5 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        eos5Parsing.parsingGame();
    }

    @Scheduled(cron = "3/4 0/5 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        eos5Parsing.closingGame();
    }

    @Scheduled(cron = "15 0/5 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        eos5Parsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        eos5Parsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isEos5() || !ZoneConfig.getEos5().isEnabled();
    }

}
