package spoon.gameZone;

import lombok.Data;

public class ZoneDto {

    @Data
    public static class Command {
        private String gameDate;
        private String league;
        private Integer round;
    }

    @Data
    public static class Bet {
        private String sdate;
        private String gameCode;
        private String betTeam;
        private int betZone;
        private long betMoney;
        private double odds;
    }

}
