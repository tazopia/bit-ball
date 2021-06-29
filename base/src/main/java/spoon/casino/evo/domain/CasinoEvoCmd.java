package spoon.casino.evo.domain;

import lombok.Data;

public class CasinoEvoCmd {

    @Data
    public static class Command {
        private String userid;
        private String casinoId;
        private String transaction;
        private String gameType;
        private String round;
    }

    @Data
    public static class Balance {
        private String userid;
        private String hash;
    }

    @Data
    public static class  Change {
        private String userid;
        private String type;
        private String gameType;
        private long amount;
        private String transaction;
        private String hash;
    }

    @Data
    public static class Cancel {
        private String transaction;
        private String hash;
    }

}
