package spoon.inPlay.odds.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.inPlay.odds.domain.Event;
import spoon.inPlay.odds.domain.Market;
import spoon.inPlay.odds.entity.InPlayOdds;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InPlayService {

    private final InPlayGameService inPlayGameService;

    private final InPlayScoreService inPlayScoreService;

    private final InPlayOddsService inPlayOddsService;

    public void game(List<Event> events) {
        events.forEach(inPlayGameService::save);
    }

    public void score(List<Event> events) {
        events.forEach(inPlayScoreService::save);
    }

    public void odds(List<Event> events) {
        for (Event event : events) {
            long fixtureId = event.getFixtureId();

            for (Market market : event.getMarkets()) {
                String oname = market.getName();
                long marketId = market.getId();

                for (Market.Provider provider : market.getProviders()) {
                    String name = provider.getName();

                    for (Market.Bet bet : provider.getBets()) {
                        InPlayOdds odds = InPlayOdds.builder()
                                .id(bet.getId())
                                .fixtureId(fixtureId)
                                .marketId(marketId)
                                .provider(name)
                                .oname(oname)
                                .name(bet.getName())
                                .line(bet.getLine())
                                .baseLine(bet.getBaseLine())
                                .status(bet.getStatus())
                                .startPrice(bet.getStartPrice())
                                .price(bet.getPrice())
                                .lastUpdate(System.currentTimeMillis())
                                .settlement(bet.getSettlement())
                                .build();
                        inPlayOddsService.save(odds);
                    }
                }
            }
        }
    }

}
