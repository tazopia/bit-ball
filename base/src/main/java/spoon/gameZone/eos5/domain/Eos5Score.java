package spoon.gameZone.eos5.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import spoon.common.utils.DateUtils;
import spoon.gameZone.eos5.entity.Eos5;

public class Eos5Score {

    @JsonIgnore
    private final Eos5 eos5;

    public String getDate() {
        return DateUtils.format(eos5.getGameDate(), "MM/dd(E) HH:mm");
    }

    public String[] getBalls() {
        return eos5.getBall().split(",");
    }

    public String getPb() {
        return eos5.getPb();
    }

    public String getPbOe() {
        return eos5.getPb_oddeven().toLowerCase();
    }

    public String getPbOu() {
        return eos5.getPb_overunder().toLowerCase();
    }

    public String getOe() {
        return eos5.getOddeven().toLowerCase();
    }

    public String getOu() {
        return eos5.getOverunder().toLowerCase();
    }

    public int getRound() {
        return eos5.getRound();
    }

    public String getSize() {
        if ("S".equals(eos5.getSize())) {
            return "s1";
        } else if ("M".equals(eos5.getSize())) {
            return "s2";
        } else {
            return "s3";
        }
    }

    public int getSum() {
        return eos5.getSum();
    }

    private Eos5Score(Eos5 eos5) {
        this.eos5 = eos5;
    }

    public static Eos5Score of(Eos5 eos5) {
        return new Eos5Score(eos5);
    }
}
