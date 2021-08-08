package spoon.casino.evolution.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CasinoEvolutionBetDto {

    private List<Bet> data = new ArrayList<>();

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Bet {
        private long id;
        private String type;
        private long amount;
        @JsonProperty("user_id")
        private long userid;
        private String status;
        private Details details;
        @JsonProperty("processed_at")
        private String processedDate;
        private User user;

        public LocalDateTime getRegDate() {
            return LocalDateTime.parse(this.processedDate.substring(0, 19), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Details {
        private Game game;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Game {
        private String id;
        private String type;
        private long round;
        private String title;
        private String vendor;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class User {
        private String username;
    }

}
