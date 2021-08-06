package spoon.casino.evolution.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class CasinoEvolutionResult {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class createUser {
        private String username;
        private String message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class User {
        private String username;
        private String nickname;
        private long balance;
        private double point;

        public long getPoint() {
            return (long) Math.floor(this.point);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Balance {
        private String username;
        private long balance; // 전체금액
        private long amount;
        @JsonProperty("transaction_id")
        private long transactionId;
        private String message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Token {
        private String token;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Game {
        private String link;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Exchange {
        private String username;
        private long balance;
        private double point;
        private String message;

        public long getPoint() {
            return (long) Math.floor(this.point);
        }
    }
}
