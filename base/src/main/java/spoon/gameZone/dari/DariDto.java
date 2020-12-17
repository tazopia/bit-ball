package spoon.gameZone.dari;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class DariDto {

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
        private boolean start;
        private boolean line;
        private boolean lineStart;

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
        private int round;
        private Date gameDate;
        private boolean cancel;

        private String start;
        private String line;
        private String oddeven;
    }
}
