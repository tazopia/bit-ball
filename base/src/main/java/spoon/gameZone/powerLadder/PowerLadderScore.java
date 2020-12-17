package spoon.gameZone.powerLadder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import spoon.common.utils.DateUtils;

public class PowerLadderScore {

    @JsonIgnore
    private PowerLadder power;

    public String getDate() {
        return DateUtils.format(power.getGameDate(), "MM/dd(E) HH:mm");
    }

    public int getRound() {
        return power.getRound();
    }

    public String getStart() {
        return power.getStart().toLowerCase();
    }

    public String getLine() {
        return power.getLine();
    }

    public String getOe() {
        return power.getOddeven().toLowerCase();
    }

    private PowerLadderScore(PowerLadder power) {
        this.power = power;
    }

    public static PowerLadderScore of(PowerLadder power) {
        return new PowerLadderScore(power);
    }
}
