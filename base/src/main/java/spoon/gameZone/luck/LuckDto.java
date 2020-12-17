package spoon.gameZone.luck;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class LuckDto {

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

        private boolean player1;
        private boolean player2;
        private boolean player3;
        private boolean color;
        private boolean pattern;

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

        private String dealer1;
        private String dealer2;
        private String player11;
        private String player12;
        private String player21;
        private String player22;
        private String player31;
        private String player32;
        private String result;

        public String getResult() {
            int dealer = convertInt(dealer2);
            String w1 = winner(dealer, player12);
            String w2 = winner(dealer, player22);
            String w3 = winner(dealer, player32);

            return w1 + w2 + w3;
        }

        private String winner(int dealer, String player) {
            int p = convertInt(player);
            if (p > dealer) {
                return "1";
            } else if (p < dealer) {
                return "0";
            } else {
                return "2";
            }
        }

        private int convertInt(String p) {
            switch (p) {
                case "1": // A는 14이다
                    return 14;
                case "2":
                    return 2;
                case "3":
                    return 3;
                case "4":
                    return 4;
                case "5":
                    return 5;
                case "6":
                    return 6;
                case "7":
                    return 7;
                case "8":
                    return 8;
                case "9":
                    return 9;
                case "T":
                    return 10;
                case "J":
                    return 11;
                case "Q":
                    return 12;
                case "K":
                    return 13;
                default:
                    return 0;
            }
        }
    }
}
