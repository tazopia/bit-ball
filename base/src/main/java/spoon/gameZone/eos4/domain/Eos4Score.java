package spoon.gameZone.eos4.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import spoon.common.utils.DateUtils;
import spoon.gameZone.eos4.entity.Eos4;

public class Eos4Score {

    @JsonIgnore
    private final Eos4 eos4;

    public String getDate() {
        return DateUtils.format(eos4.getGameDate(), "MM/dd(E) HH:mm");
    }

    public String[] getBalls() {
        return eos4.getBall().split(",");
    }

    public String getPb() {
        return eos4.getPb();
    }

    public String getPbOe() {
        return eos4.getPb_oddeven().toLowerCase();
    }

    public String getPbOu() {
        return eos4.getPb_overunder().toLowerCase();
    }

    public String getOe() {
        return eos4.getOddeven().toLowerCase();
    }

    public String getOu() {
        return eos4.getOverunder().toLowerCase();
    }

    public int getRound() {
        return eos4.getRound();
    }

    public String getSize() {
        if ("S".equals(eos4.getSize())) {
            return "s1";
        } else if ("M".equals(eos4.getSize())) {
            return "s2";
        } else {
            return "s3";
        }
    }

    public int getSum() {
        return eos4.getSum();
    }

    private Eos4Score(Eos4 eos4) {
        this.eos4 = eos4;
    }

    public static Eos4Score of(Eos4 eos4) {
        return new Eos4Score(eos4);
    }
}
