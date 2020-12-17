package spoon.gameZone.KenoLadder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import spoon.common.utils.DateUtils;

public class KenoLadderScore {

    @JsonIgnore
    private KenoLadder power;

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

    private KenoLadderScore(KenoLadder power) {
        this.power = power;
    }

    public static KenoLadderScore of(KenoLadder power) {
        return new KenoLadderScore(power);
    }
}
