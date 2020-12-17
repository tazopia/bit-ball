package spoon.inPlay.odds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LiveScore {

    @JsonProperty("Scoreboard")
    private ScoreBoard scoreBoard;

    @JsonProperty("Periods")
    private List<Period> periods;

    @JsonProperty("Statistics")
    private List<Statistics> statistics;

    @JsonProperty("LivescoreExtraData")
    private List<ExtraData> livescoreExtraData;

    // ---------------------------------------------

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ScoreBoard {
        @JsonProperty("Status")
        private int status;

        @JsonProperty("CurrentPeriod")
        private int currentPeriod;

        @JsonProperty("Time")
        private int time;

        @JsonProperty("Results")
        private List<Result> results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Period {

        @JsonProperty("Type")
        private int type;

        @JsonProperty("IsFinished")
        private boolean finished;

        @JsonProperty("Confirmed")
        private boolean confirmed;

        @JsonProperty("Results")
        private List<Result> results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Statistics {
        @JsonProperty("Type")
        private int type;

        @JsonProperty("Results")
        private List<Result> results;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ExtraData {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Value")
        private String value;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Result {
        @JsonProperty("Position")
        private int position;

        @JsonProperty("Value")
        private String value;
    }
}
