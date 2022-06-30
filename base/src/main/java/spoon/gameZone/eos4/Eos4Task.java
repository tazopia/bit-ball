package spoon.gameZone.eos4;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos4.bot.Eos4Parsing;

@Slf4j
@RequiredArgsConstructor
@Component
public class Eos4Task {

    private final Eos4Parsing eos4Parsing;

    @Scheduled(cron = "0 0/4 * * * *")
    public void parsingGame() {
        if (notParsing()) return;
        eos4Parsing.parsingGame();
    }

    @Scheduled(cron = "3/4 0/4 * * * *")
    public void parsingResult() {
        if (notParsing()) return;
        eos4Parsing.closingGame();
    }

    @Scheduled(cron = "15 0/4 * * * *")
    public void checkResult() {
        if (notParsing()) return;
        eos4Parsing.checkResult();
    }

    @Scheduled(cron = "7 1 4 * * * ")
    public void deleteGame() {
        eos4Parsing.deleteGame();
    }

    private boolean notParsing() {
        return !Config.getSysConfig().getZone().isEnabled() || !Config.getSysConfig().getZone().isEos4() || !ZoneConfig.getEos4().isEnabled();
    }

}
