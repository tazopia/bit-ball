package spoon.gameZone.eos1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos1.bot.Eos1Parsing;

@Slf4j
@RequiredArgsConstructor
@Component
public class Eos1Task {

    private final Eos1Parsing eos1Parsing;

    @Scheduled(cron = "0 * * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        eos1Parsing.parsingGame();
    }

    @Scheduled(cron = "3/4 * * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        eos1Parsing.closingGame();
    }

    @Scheduled(cron = "15 * * * * *")
    public void checkResult() {
        if (notParsing()) return;
        eos1Parsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        eos1Parsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isEos1() || !ZoneConfig.getEos1().isEnabled();
    }

}
