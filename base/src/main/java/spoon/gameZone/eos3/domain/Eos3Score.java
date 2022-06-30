package spoon.gameZone.eos3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import spoon.common.utils.DateUtils;
import spoon.gameZone.eos3.entity.Eos3;

public class Eos3Score {

    @JsonIgnore
    private final Eos3 eos3;

    public String getDate() {
        return DateUtils.format(eos3.getGameDate(), "MM/dd(E) HH:mm");
    }

    public String[] getBalls() {
        return eos3.getBall().split(",");
    }

    public String getPb() {
        return eos3.getPb();
    }

    public String getPbOe() {
        return eos3.getPb_oddeven().toLowerCase();
    }

    public String getPbOu() {
        return eos3.getPb_overunder().toLowerCase();
    }

    public String getOe() {
        return eos3.getOddeven().toLowerCase();
    }

    public String getOu() {
        return eos3.getOverunder().toLowerCase();
    }

    public int getRound() {
        return eos3.getRound();
    }

    public String getSize() {
        if ("S".equals(eos3.getSize())) {
            return "s1";
        } else if ("M".equals(eos3.getSize())) {
            return "s2";
        } else {
            return "s3";
        }
    }

    public int getSum() {
        return eos3.getSum();
    }

    private Eos3Score(Eos3 eos3) {
        this.eos3 = eos3;
    }

    public static Eos3Score of(Eos3 eos3) {
        return new Eos3Score(eos3);
    }
}
