package spoon.gameZone.eos3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos3.bot.Eos3Parsing;

@Slf4j
@RequiredArgsConstructor
@Component
public class Eos3Task {

    private final Eos3Parsing eos3Parsing;

    @Scheduled(cron = "0 0/3 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        eos3Parsing.parsingGame();
    }

    @Scheduled(cron = "3/4 0/3 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        eos3Parsing.closingGame();
    }

    @Scheduled(cron = "15 0/3 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        eos3Parsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        eos3Parsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isEos3() || !ZoneConfig.getEos3().isEnabled();
    }

}
