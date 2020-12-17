package spoon.gameZone.soccer;

import lombok.Data;
import spoon.common.utils.DateUtils;
import spoon.gameZone.ZoneConfig;

import java.util.Date;

public class SoccerDto {

    @Data
    public static class Config {
        private long money;
        private boolean enabled;
        private int max;
        private int win;
        private int min;
        private int betTime;

        private boolean ma;
        private boolean ah;
        private boolean ou;
    }

    @Data
    public static class Score {
        private long id;
        private boolean cancel;
        private boolean closing;
        private Date gameDate;
        private String league;
        private String teamHome;
        private String teamAway;

        private Integer scoreHome;
        private Integer scoreAway;
    }

    @Data
    public static class List {
        private long id;
        private Date gameDate;
        private String league;
        private String teamHome;
        private String teamAway;
        private double[] odds = new double[7];
        private double handy;
        private double overunder;
        private String sdate;

        public List(Soccer s) {
            this.id = s.getId();
            this.gameDate = s.getGameDate();
            this.league = s.getLeagueName();
            this.teamHome = s.getTeamHome();
            this.teamAway = s.getTeamAway();
            this.odds[0] = s.getMaHomeOdds();
            this.odds[1] = s.getMaDrawOdds();
            this.odds[2] = s.getMaAwayOdds();
            this.odds[3] = s.getAhHomeOdds();
            this.odds[4] = s.getAhAwayOdds();
            this.odds[5] = s.getOuHomeOdds();
            this.odds[6] = s.getOuAwayOdds();
            this.overunder = s.getOuDraw();
            this.handy = s.getAhDraw();
            this.sdate = s.getSdate();
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
    }
}
