package spoon.gameZone.eos1.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import spoon.common.utils.DateUtils;
import spoon.gameZone.eos1.entity.Eos1;

public class Eos1Score {

    @JsonIgnore
    private final Eos1 eos1;

    public String getDate() {
        return DateUtils.format(eos1.getGameDate(), "MM/dd(E) HH:mm");
    }

    public String[] getBalls() {
        return eos1.getBall().split(",");
    }

    public String getPb() {
        return eos1.getPb();
    }

    public String getPbOe() {
        return eos1.getPb_oddeven().toLowerCase();
    }

    public String getPbOu() {
        return eos1.getPb_overunder().toLowerCase();
    }

    public String getOe() {
        return eos1.getOddeven().toLowerCase();
    }

    public String getOu() {
        return eos1.getOverunder().toLowerCase();
    }

    public int getRound() {
        return eos1.getRound();
    }

    public String getSize() {
        if ("S".equals(eos1.getSize())) {
            return "s1";
        } else if ("M".equals(eos1.getSize())) {
            return "s2";
        } else {
            return "s3";
        }
    }

    public int getSum() {
        return eos1.getSum();
    }

    private Eos1Score(Eos1 eos1) {
        this.eos1 = eos1;
    }

    public static Eos1Score of(Eos1 eos1) {
        return new Eos1Score(eos1);
    }
}
