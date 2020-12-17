package spoon.gameZone.power;

import com.fasterxml.jackson.annotation.JsonIgnore;
import spoon.common.utils.DateUtils;

import java.util.Arrays;
import java.util.List;

public class PowerScore {

    @JsonIgnore
    private Power power;

    public String getDate() {
        return DateUtils.format(power.getGameDate(), "MM/dd(E) HH:mm");
    }

    public List<String> getTimes() {
        String t = String.valueOf(power.getTimes());
        return Arrays.asList(t.substring(0, 4), t.substring(4, 7));
    }

    public String[] getBalls() {
        return power.getBall().split(",");
    }

    public String getPb() {
        return power.getPb();
    }

    public String getPbOe() {
        return power.getPb_oddeven().toLowerCase();
    }

    public String getPbOu() {
        return power.getPb_overunder().toLowerCase();
    }

    public String getOe() {
        return power.getOddeven().toLowerCase();
    }

    public String getOu() {
        return power.getOverunder().toLowerCase();
    }

    public String getSize() {
        if ("소".equals(power.getSize())) {
            return "s1";
        } else if ("중".equals(power.getSize())) {
            return "s2";
        } else {
            return "s3";
        }
    }

    public int getSum() {
        return power.getSum();
    }

    private PowerScore(Power power) {
        this.power = power;
    }

    public static PowerScore of(Power power) {
        return new PowerScore(power);
    }
}
