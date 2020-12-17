package spoon.member.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.gameZone.ZoneConfig;
import spoon.member.entity.Member;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class CurrentUser implements Serializable {

    private long id;

    private String userid;

    private String password;

    private String nickname;

    private Role role;

    private int level;

    private String agency1;

    private String agency2;

    private String agency3;

    private String agency4;

    private String loginIp;

    private String loginDomain;

    private Date loginDate;

    private String device;

    private boolean enabled;

    public CurrentUser(Member member) {
        this.id = member.getId();
        this.userid = member.getUserid();
        this.password = member.getPassword();
        this.nickname = member.getNickname();
        this.role = member.getRole();
        this.level = member.getLevel();
        this.agency1 = member.getAgency1();
        this.agency2 = member.getAgency2();
        this.agency3 = member.getAgency3();
        this.agency4 = member.getAgency4();
        this.loginIp = member.getLoginIp();
        this.loginDomain = member.getLoginDomain();
        this.device = member.getLoginDevice();
        this.enabled = member.isEnabled() && !member.isSecession();
        this.loginDate = member.getLoginDate();
    }

    public String getSunid() {
        return ZoneConfig.getSun().getPrefix() + String.format("%05d", this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentUser that = (CurrentUser) o;

        return userid != null ? userid.equals(that.userid) : that.userid == null;
    }

    @Override
    public int hashCode() {
        return userid != null ? userid.hashCode() : 0;
    }
}
