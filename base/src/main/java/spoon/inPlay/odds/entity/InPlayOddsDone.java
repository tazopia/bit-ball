package spoon.inPlay.odds.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "INPLAY_ODDS_DONE")
public class InPlayOddsDone {

    @Id
    private Long id;

    private long fixtureId;

    private long marketId;

    @Nationalized
    private String provider;

    @Nationalized
    private String oname;

    @Nationalized
    private String name;

    private String line;

    private String baseLine;

    private int status;

    private double startPrice;

    private double price;

    private int settlement;

    private long lastUpdate;

    private InPlayOddsDone(InPlayOdds odds) {
        id = odds.getId();
        fixtureId = odds.getFixtureId();
        marketId = odds.getMarketId();
        provider = odds.getProvider();
        oname = odds.getOname();
        name = odds.getName();
        line = odds.getLine();
        baseLine = odds.getBaseLine();
        status = odds.getStatus();
        startPrice = odds.getStartPrice();
        price = odds.getPrice();
        settlement = odds.getSettlement();
        lastUpdate = System.currentTimeMillis();
    }

    public static InPlayOddsDone of(InPlayOdds odds) {
        return new InPlayOddsDone(odds);
    }
}
