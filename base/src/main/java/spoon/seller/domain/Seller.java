package spoon.seller.domain;

import lombok.Data;
import spoon.member.domain.Role;

@Data
public class Seller {

    private String userid;

    private String nickname;

    private Role role;

    private String agency2;

    private String agency1;

    private String agency3;

    private String agency4;

    private String rateCode;

    private double rateShare;

    private double rateSports;

    private double rateZone;

    private long inMoney;

    private long outMoney;

    private long regMember;

    private long joinMember;

    private long betSports;

    private long betZone;

    // -------------------------------------

    private double powerMax;
    private double powerMin;

    private double powerLadder;
    private double keno;
    private double kenoLadder;

    private double btc3Max;
    private double btc3Min;
    private double btc5Max;
    private double btc5Min;

    private double casino;

    public int getHq() {
        if (this.role.ordinal() >= Role.AGENCY4.ordinal()) return 4;
        if (this.role.ordinal() >= Role.AGENCY3.ordinal()) return 3;
        if (this.role.ordinal() >= Role.AGENCY2.ordinal()) return 2;
        return 1;
    }

}
