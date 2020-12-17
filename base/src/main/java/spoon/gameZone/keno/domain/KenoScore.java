package spoon.gameZone.keno.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;
import spoon.gameZone.keno.entity.Keno;

public class KenoScore {

    @JsonIgnore
    private Keno keno;

    public String getDate() {
        return DateUtils.format(keno.getGameDate(), "MM/dd(E) HH:mm");
    }

    public int getRound() {
        return keno.getRound();
    }

    public int getSum() {
        return keno.getSum();
    }

    public String getLast() {
        String sum = String.valueOf(keno.getSum());
        if (StringUtils.empty(sum)) return "";
        return sum.substring(sum.length() - 1);
    }

    public String getOu() {
        return keno.getOverunder().toLowerCase();
    }

    public String getOe() {
        return keno.getOddeven().toLowerCase();
    }

    private KenoScore(Keno keno) {
        this.keno = keno;
    }

    public static KenoScore of(Keno keno) {
        return new KenoScore(keno);
    }
}
