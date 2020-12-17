package spoon.gameZone.keno.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class KenoDto {

    @Data
    public static class Config {
        private long money;
        private boolean enabled;
        private int round;
        private Date gameDate;
        private int betTime;
        private String sdate;
        private double[] odds;
        private int max;
        private int win;
        private int min;

        private boolean oddeven;
        private boolean overunder;

        public String getGameDateName() {
            return DateUtils.format(gameDate, "MM/dd(E)");
        }

        public String getGameTimeName() {
            return DateUtils.format(gameDate, "HH:mm");
        }
    }

    @Data
    public static class Score {
        private long id;
        private String sdate;
        private int round;
        private boolean cancel;

        private int sum;
        private String overunder;
        private String oddeven;

        public Date getGameDate() {
            return DateUtils.parse(this.sdate, "yyyyMMddHHmm");
        }
    }
}
