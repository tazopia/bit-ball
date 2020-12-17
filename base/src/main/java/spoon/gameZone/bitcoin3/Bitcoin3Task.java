package spoon.gameZone.bitcoin3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.bitcoin3.bot.Bitcoin3Parsing;

@Slf4j
@RequiredArgsConstructor
@Component
public class Bitcoin3Task {

    private final Bitcoin3Parsing bitcoin3Parsing;

    @Scheduled(cron = "3 0/3 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        bitcoin3Parsing.addGame();
    }

    @Scheduled(cron = "1/2 0/3 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        bitcoin3Parsing.closingGame();
    }

    @Scheduled(cron = "30 0/3 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        bitcoin3Parsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        bitcoin3Parsing.deleteGame(2);
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isBitcoin3() || !ZoneConfig.getBitcoin3().isEnabled();
    }

}
