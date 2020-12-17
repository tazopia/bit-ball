package spoon.inPlay.bet.domain;

import lombok.Data;

public class InPlayBetDto {

    @Data
    public static class Command {
        private Long gameId;
        private String betDate = "";
        private String role = "";
        private String result = "";
        private String username = "";
        private boolean match;
        private String orderBy = "";
        private String userid = "";
        private String ip = "";
    }

    @Data
    public static class Bet {
        private long fixtureId;
        private long id;
        private long money;
        private double odds;
        private String corner;
        private String score;
        private String team;
        private int period;
        private boolean force;
    }

}
