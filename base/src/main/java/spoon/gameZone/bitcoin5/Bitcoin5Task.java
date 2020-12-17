package spoon.gameZone.bitcoin5;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.bitcoin5.bot.Bitcoin5Parsing;

@Slf4j
@RequiredArgsConstructor
@Component
public class Bitcoin5Task {

    private final Bitcoin5Parsing bitcoin5Parsing;

    @Scheduled(cron = "3 0/5 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        bitcoin5Parsing.addGame();
    }

    @Scheduled(cron = "1/2 0/5 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        bitcoin5Parsing.closingGame();
    }

    @Scheduled(cron = "30 0/5 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        bitcoin5Parsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        bitcoin5Parsing.deleteGame(2);
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isBitcoin5() || !ZoneConfig.getBitcoin5().isEnabled();
    }

}
