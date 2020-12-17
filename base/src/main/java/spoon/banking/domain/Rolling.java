package spoon.banking.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
public class Rolling {

    private String userid;

    private long money;

    private long point;

    // 마지막 충전금액
    private long amount;

    private long bonusPoint;

    private long betSports;

    private long betZone;

    private Date lastDate;

    @JsonIgnore
    public long getRollingSports() {
        return this.amount == 0 ? 0 : (long) (this.betSports * 100D / this.amount);
    }

    @JsonIgnore
    public long getRollingZone() {
        return this.amount == 0 ? 0 : (long) (this.betZone * 100D / this.amount);
    }

    @JsonIgnore
    public boolean isBetting() {
        return betSports + betZone >= bonusPoint;
    }
}
