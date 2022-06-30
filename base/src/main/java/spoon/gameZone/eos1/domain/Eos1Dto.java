package spoon.gameZone.eos1.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class Eos1Dto {

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
        private boolean pb_oddeven;
        private boolean overunder;
        private boolean pb_overunder;
        private boolean size;
        private boolean oe_ou;
        private boolean oe_size;
        private boolean pb_oe_ou;

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

        private String pb;
        private String ball;
    }


}
