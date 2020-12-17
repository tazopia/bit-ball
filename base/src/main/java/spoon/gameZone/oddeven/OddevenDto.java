package spoon.gameZone.oddeven;

import lombok.Data;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;

import java.util.Date;

public class OddevenDto {

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

        private String card1;
        private String card2;
        private String oddeven;
        private String overunder;
        private String pattern;

        private String card;

        /**
         * 봇 결과 두카드를 받아서 결과처리의 card형태로 반환한다.
         */
        public void convertCard(String card1, String card2) {
            this.card1 = card1;
            this.card2 = card2;
            this.card = bot2score(card1) + "+" + bot2score(card2);
            if ("+".equals(this.card)) {
                this.card = "";
            }
        }

        /**
         * 결과처리 형태를 봇 형태로 전환한다.
         */
        public boolean convertCard() {
            try {
                String card1 = this.card.split("\\+")[0].trim(); // 히든카드
                String card2 = this.card.split("\\+")[1].trim();

                String p1 = convertPattern(card1.substring(0, 1));
                String p2 = convertPattern(card2.substring(0, 1));
                int c1 = convertNumber(card1.substring(1));
                int c2 = convertNumber(card2.substring(1));
                this.overunder = c1 > 5 ? "OVER" : "UNDER";
                this.oddeven = (c1 + c2) % 2 == 0 ? "E" : "O";
                this.pattern = convertPatternResult(p1);
                this.card1 = score2bot(p1, c1);
                this.card2 = score2bot(p2, c2);

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private String score2bot(String p, int c) {
            if (c == 1) c = 14;
            return String.format("%s%02d", p, c);
        }

        private String bot2score(String card) {
            if (StringUtils.empty(card)) return "";
            if (card.length() != 3) return "";
            if ("---".equals(card)) return "";

            String card1 = card.substring(0, 1);
            String card2 = String.valueOf(Integer.parseInt(card.substring(1), 10));
            if ("14".equals(card2)) card2 = "A";
            switch (card1) {
                case "1":
                    return "♠" + card2;
                case "2":
                    return "♥" + card2;
                case "3":
                    return "♣" + card2;
                case "4":
                    return "◆" + card2;
                default:
                    return "";
            }
        }

        private String convertPattern(String p) {
            switch (p) {
                case "♠":
                    return "1";
                case "♥":
                    return "2";
                case "♣":
                    return "3";
                case "◆":
                    return "4";
                default:
                    return "";
            }
        }

        private String convertPatternResult(String p) {
            switch (p) {
                case "1":
                    return "스페이드";
                case "2":
                    return "하트";
                case "3":
                    return "크로바";
                case "4":
                    return "다이아";
                default:
                    return "";
            }
        }

        private int convertNumber(String p) {
            if ("A".equals(p)) p = "1";
            return Integer.parseInt(p, 10);
        }
    }
}
