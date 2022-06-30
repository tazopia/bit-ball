package spoon.gameZone.eos2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos2.bot.Eos2Parsing;

@Slf4j
@RequiredArgsConstructor
@Component
public class Eos2Task {

    private final Eos2Parsing eos2Parsing;

    @Scheduled(cron = "0 0/2 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        eos2Parsing.parsingGame();
    }

    @Scheduled(cron = "3/4 0/2 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        eos2Parsing.closingGame();
    }

    @Scheduled(cron = "15 0/2 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        eos2Parsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        eos2Parsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isEos2() || !ZoneConfig.getEos2().isEnabled();
    }

}
