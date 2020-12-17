package spoon.gameZone.bitcoin1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.bitcoin1.bot.Bitcoin1Parsing;

@Slf4j
@RequiredArgsConstructor
@Component
public class Bitcoin1Task {

    private final Bitcoin1Parsing bitcoin1Parsing;

    @Scheduled(cron = "3 0/3 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        bitcoin1Parsing.addGame();
    }

    @Scheduled(cron = "1/2 * * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        bitcoin1Parsing.closingGame();
    }

    @Scheduled(cron = "30 * * * * *")
    public void checkResult() {
        if (notParsing()) return;
        bitcoin1Parsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        bitcoin1Parsing.deleteGame(2);
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isBitcoin1() || !ZoneConfig.getBitcoin1().isEnabled();
    }

}
