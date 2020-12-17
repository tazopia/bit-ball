package spoon.gameZone.dog;

import lombok.Data;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;

import java.util.Date;

public class DogDto {

    @Data
    public static class Config {
        private long money;
        private boolean enabled;
        private int max;
        private int win;
        private int min;
        private int betTime;
    }

    @Data
    public static class Score {
        private long id;
        private Date gameDate;
        private String league;
        private boolean cancel;
        private Integer winNumber;
    }

    @Data
    public static class List {
        private long id;
        private Date gameDate;
        private String league;
        private String[] team = new String[6];
        private double[] odds = new double[6];
        private String sdate;

        public List(Dog d) {
            this.id = d.getId();
            this.gameDate = d.getGameDate();
            this.league = d.getLeague();
            this.team[0] = d.getTeam1();
            this.team[1] = d.getTeam2();
            this.team[2] = d.getTeam3();
            this.team[3] = d.getTeam4();
            this.team[4] = d.getTeam5();
            this.team[5] = d.getTeam6();
            this.odds[0] = d.getOdds1();
            this.odds[1] = d.getOdds2();
            this.odds[2] = d.getOdds3();
            this.odds[3] = d.getOdds4();
            this.odds[4] = d.getOdds5();
            this.odds[5] = d.getOdds6();
            this.sdate = d.getSdate();
        }

        public String getGameDateName() {
            return DateUtils.format(this.gameDate, "MM/dd(E)");
        }

        public String getGameTimeName() {
            return DateUtils.format(this.gameDate, "HH:mm");
        }

        public long getBetTime() {
            return this.gameDate.getTime() - (ZoneConfig.getSoccer().getBetTime() * 1000);
        }
    }

    @Data
    public static class Command {
        private Long id;
        private String league;
        private String sdate;
    }

}
