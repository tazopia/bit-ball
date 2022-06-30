package spoon.gameZone.eos2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import spoon.common.utils.DateUtils;
import spoon.gameZone.eos2.entity.Eos2;

public class Eos2Score {

    @JsonIgnore
    private final Eos2 eos2;

    public String getDate() {
        return DateUtils.format(eos2.getGameDate(), "MM/dd(E) HH:mm");
    }

    public String[] getBalls() {
        return eos2.getBall().split(",");
    }

    public String getPb() {
        return eos2.getPb();
    }

    public String getPbOe() {
        return eos2.getPb_oddeven().toLowerCase();
    }

    public String getPbOu() {
        return eos2.getPb_overunder().toLowerCase();
    }

    public String getOe() {
        return eos2.getOddeven().toLowerCase();
    }

    public String getOu() {
        return eos2.getOverunder().toLowerCase();
    }

    public int getRound() {
        return eos2.getRound();
    }

    public String getSize() {
        if ("S".equals(eos2.getSize())) {
            return "s1";
        } else if ("M".equals(eos2.getSize())) {
            return "s2";
        } else {
            return "s3";
        }
    }

    public int getSum() {
        return eos2.getSum();
    }

    private Eos2Score(Eos2 eos2) {
        this.eos2 = eos2;
    }

    public static Eos2Score of(Eos2 eos2) {
        return new Eos2Score(eos2);
    }
}
