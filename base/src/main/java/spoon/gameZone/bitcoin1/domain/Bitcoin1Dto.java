package spoon.gameZone.bitcoin1.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class Bitcoin1Dto {

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

        private boolean hi_oe;
        private boolean hi_ou;
        private boolean hi_oe_ou;
        private boolean lo_oe;
        private boolean lo_ou;
        private boolean lo_oe_ou;

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
        private String sdate;

        private String bs = "";

        private double open;

        private double close;

        private double high;

        private double low;
    }
}
