package spoon.inPlay.odds.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "INPLAY_ODDS")
public class InPlayOdds {

    @Id
    private Long id;

    private long fixtureId;

    private long marketId;

    @JsonIgnore
    @Nationalized
    private String provider;

    @Nationalized
    private String oname;

    @Nationalized
    private String name;

    private String line;

    private String baseLine;

    private int status;

    @JsonIgnore
    private double startPrice;

    @JsonProperty("odds")
    private double price;

    @JsonIgnore
    private int settlement;

    @JsonProperty("ut")
    private long lastUpdate;

    public void updateSettlement(int settlement) {
        this.settlement = settlement;
    }
}
