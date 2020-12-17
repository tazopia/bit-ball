package spoon.inPlay.odds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Event {

    @JsonProperty("FixtureId")
    private long fixtureId;

    @JsonProperty("Fixture")
    private Fixture fixture;

    @JsonProperty("Livescore")
    private LiveScore liveScore;

    @JsonProperty("Markets")
    private List<Market> markets;


}
