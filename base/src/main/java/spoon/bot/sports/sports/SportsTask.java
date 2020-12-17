package spoon.bot.sports.sports;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.bot.sports.service.ParsingGame;
import spoon.bot.sports.service.ParsingResult;
import spoon.config.domain.Config;

@Slf4j
@RequiredArgsConstructor
@Component
public class SportsTask {

    private final ParsingGame sportsParsingGame;

    private final ParsingResult sportsParsingResult;

    @Scheduled(cron = "12/30 * * * * *")
    public void parsingGame() {
        if ("none".equals(Config.getSysConfig().getSports().getSports())) return;
        sportsParsingGame.parsingGame();
    }

    @Scheduled(cron = "22/30 * * * * *")
    public void parsingResult() {
        if ("none".equals(Config.getSysConfig().getSports().getSports())) return;
        sportsParsingResult.closingGame();
    }

}
