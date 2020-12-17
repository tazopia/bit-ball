package spoon.inPlay.config.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InPlayMinMax {

    @JsonProperty("Football")
    private MinMax football = new MinMax();

    @JsonProperty("Baseball")
    private MinMax baseball = new MinMax();

    @JsonProperty("Boxing")
    private MinMax boxing = new MinMax();

    @JsonProperty("E-Games")
    private MinMax egames = new MinMax();

    @JsonProperty("Basketball")
    private MinMax basketball = new MinMax();

    @JsonProperty("Ice Hockey")
    private MinMax iceHockey = new MinMax();

    @JsonProperty("Volleyball")
    private MinMax volleyball = new MinMax();

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class MinMax {
        private double min;
        private double max;
    }

}
