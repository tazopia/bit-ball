package spoon.member.domain;

import lombok.Data;

@Data
public class User {

    private String userid;

    private String nickname;

    private String agency1;

    private String agency2;

    private String agency3;

    private String agency4;

    private Role role;

    private int level;

    private int memo;

    private int qna;

    private long money;

    private long point;

    private boolean enabled;

    private boolean secession;

    private boolean block;

    private boolean black;

    private String loginIp;

    public long getMilliseconds() {
        return System.currentTimeMillis();
    }

}
