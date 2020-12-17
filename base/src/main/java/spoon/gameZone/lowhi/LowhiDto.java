package spoon.gameZone.lowhi;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class LowhiDto {

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
        private boolean lowhi;
        private boolean lowhiOddeven;

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

        private String oddeven;
        private String lowhi;
    }
}
