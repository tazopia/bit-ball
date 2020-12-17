package spoon.game.domain;

import lombok.Data;

public class SportsDto {

    @Data
    public static class Add {
        private String sportsName;
        private double oddsMatch = 100;
        private double oddsHandicap = 100;
        private double oddsOverUnder = 100;
        private double oddsPlusMatch = 0;
        private double oddsPlusHandicap = 0;
        private double oddsPlusOverUnder = 0;
        private boolean hidden;
    }

    @Data
    public static class Update {
        private long id;
        private double oddsMatch;
        private double oddsHandicap;
        private double oddsOverUnder;
        private double oddsPlusMatch = 0;
        private double oddsPlusHandicap = 0;
        private double oddsPlusOverUnder = 0;
        private boolean hidden;
    }
}
