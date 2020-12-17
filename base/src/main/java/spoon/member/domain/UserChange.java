package spoon.member.domain;

import lombok.Data;

import java.util.Date;

@Data
public class UserChange {

    private String role;
    private int level;
    private String userid;
    private String password;
    private String nickname;
    private String phone;
    private String agency1;
    private String agency2;
    private String agency3;
    private String agency4;
    private String joinDomain;
    private long money;
    private long point;
    private Date joinDate;
    private String joinIp;
    private Date loginDate;
    private String loginIp;
    private boolean enabled;
    private boolean secession;
    private String bank;
    private String depositor;
    private String account;
    private String bankPassword;
    private String memo;
    private String recommender;
    private String joinCode;
    private String rateCode;
    private double rateShare;
    private double rateSports;
    private double rateZone;
    private long deposit;
    private long withdraw;
    private long betSports;
    private long betSportsCnt;
    private long loginCnt;
    private int balance;
}
