package spoon.bot.sports.best;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.bot.sports.service.ParsingGame;
import spoon.bot.sports.service.ParsingResult;
import spoon.config.domain.Config;

@Slf4j
@AllArgsConstructor
@Component
public class BestTask {

    private ParsingGame bestParsingGame;

    private ParsingResult bestParsingResult;

    @Scheduled(cron = "10/30 * * * * *")
    public void parsingGame() {
        if ("none".equals(Config.getSysConfig().getSports().getBest())) return;
        bestParsingGame.parsingGame();
    }

    @Scheduled(cron = "20/30 * * * * *")
    public void parsingResult() {
        if ("none".equals(Config.getSysConfig().getSports().getBest())) return;
        bestParsingResult.closingGame();
    }

}
