package spoon.bot.sports.ferrari;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.bot.sports.service.ParsingGame;
import spoon.bot.sports.service.ParsingResult;
import spoon.config.domain.Config;

@RequiredArgsConstructor
@Component
public class FerrariTask {

    private final ParsingGame ferrariParsingGame;

    private final ParsingResult ferrariParsingResult;

    @Scheduled(cron = "6/30 * * * * *")
    public void parsingGame() {
        if ("none".equals(Config.getSysConfig().getSports().getFerrari())) return;
        ferrariParsingGame.parsingGame();
    }

    @Scheduled(cron = "26/30 * * * * *")
    public void parsingResult() {
        if ("none".equals(Config.getSysConfig().getSports().getFerrari())) return;
        ferrariParsingResult.closingGame();
    }

}
