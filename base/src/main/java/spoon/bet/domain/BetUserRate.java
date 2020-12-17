package spoon.bet.domain;

import lombok.Data;

@Data
public class BetUserRate {

    private String userid;
    private String nickname;
    private String agency1;
    private String agency2;
    private String agency3;
    private String agency4;
    private int level;

    private long cntSports;
    private long betSports;
    private long hitSports;

    private long cntZone;
    private long betZone;
    private long hitZone;

    private long betSum;

    public String getRateSports() {
        if (betSum == 0) return "0.00";
        return String.format("%.2f", (betSports * 100D) / betSum) + "%";
    }

    public String getRateZone() {
        if (betSum == 0) return "0.00";
        return String.format("%.2f", (betZone * 100D) / betSum) + "%";
    }
}
