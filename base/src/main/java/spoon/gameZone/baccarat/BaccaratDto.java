package spoon.gameZone.baccarat;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class BaccaratDto {

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

        private String card;

        private String p1;
        private String p2;
        private String p3 = "";
        private String b1;
        private String b2;
        private String b3 = "";
        private String result;

        /**
         * 봇 결과를 관리자 입력 결과 형태로 바꾼다.
         */
        public void convertCard(String p1, String p2, String p3, String b1, String b2, String b3) {
            String player = getString(p1) + getString(p2) + getString(p3);
            String banker = getString(b1) + getString(b2) + getString(b3);
            this.card = player + System.getProperty("line.separator") + banker;
        }

        private String getString(String card) {
            if (StringUtils.empty(card) || card.length() != 3) {
                return "";
            }
            int num = Integer.parseInt(card.substring(1));
            switch (num) {
                case 11:
                    return "J";
                case 12:
                    return "Q";
                case 13:
                    return "K";
                case 14:
                    return "A";
                default:
                    return String.valueOf(num);
            }
        }

        /**
         * 관리자 입력 결과를 봇 결과로 바꾼다.
         */
        public boolean convertCard() {
            List<String> list = Stream.of(card.split(System.getProperty("line.separator"))).filter(x -> x.trim() != null && !"".equals(x)).collect(Collectors.toList());
            if (list.size() != 2) {
                return false;
            }

            String[] card1 = list.get(0).replaceAll("10", "X").split(""); // player
            String[] card2 = list.get(1).replaceAll("10", "X").split(""); // banker
            int sum1 = 0;
            int sum2 = 0;
            this.p3 = "0";
            this.b3 = "0";

            for (int i = 0; i < card1.length; i++) {
                String c;
                switch (card1[i]) {
                    case "A":
                        sum1 += 1;
                        c = "114";
                        break;
                    case "X":
                        c = "110";
                        break;
                    case "J":
                        c = "111";
                        break;
                    case "Q":
                        c = "112";
                        break;
                    case "K":
                        c = "113";
                        break;
                    default:
                        sum1 += Integer.parseInt(card1[i]);
                        c = "10" + card1[i];
                        break;
                }

                if (i == 0) {
                    p1 = c;
                } else if (i == 1) {
                    p2 = c;
                } else {
                    p3 = c;
                }
            }

            for (int i = 0; i < card2.length; i++) {
                String c;
                switch (card2[i]) {
                    case "A":
                        sum2 += 1;
                        c = "114";
                        break;
                    case "X":
                        c = "110";
                        break;
                    case "J":
                        c = "111";
                        break;
                    case "Q":
                        c = "112";
                        break;
                    case "K":
                        c = "113";
                        break;
                    default:
                        sum2 += Integer.parseInt(card2[i]);
                        c = "10" + card2[i];
                        break;
                }

                if (i == 0) {
                    b1 = c;
                } else if (i == 1) {
                    b2 = c;
                } else {
                    b3 = c;
                }
            }

            sum1 = sum1 % 10;
            sum2 = sum2 % 10;

            if (sum1 > sum2) {
                this.result = "P";
            } else if (sum1 < sum2) {
                this.result = "B";
            } else {
                this.result = "T";
            }
            return true;
        }
    }
}
