package spoon.member.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.common.utils.WebUtils;
import spoon.member.domain.Role;
import spoon.member.domain.User;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "LOGIN_HISTORY", indexes = {
        @Index(name = "IDX_userid", columnList = "userid"),
        @Index(name = "IDX_loginDate", columnList = "loginDate")
})
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    private String userid;

    @Column(columnDefinition = "nvarchar(64)")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private Role role;

    private int level;

    @Column(columnDefinition = "nvarchar(64)")
    private String agency1;

    @Column(columnDefinition = "nvarchar(64)")
    private String agency2;

    @Column(columnDefinition = "nvarchar(64)")
    private String agency3;

    @Column(columnDefinition = "nvarchar(64)")
    private String agency4;

    private String domain;

    @Column(length = 64)
    private String ip;

    @Column(length = 64)
    private String device;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date loginDate;

    @Temporal(value = TemporalType.DATE)
    private Date loginDay;

    public LoginHistory(User user) {
        this.userid = user.getUserid();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.level = user.getLevel();
        this.agency1 = user.getAgency1();
        this.agency2 = user.getAgency2();
        this.agency3 = user.getAgency3();
        this.agency4 = user.getAgency4();
        this.domain = WebUtils.domain();
        this.ip = WebUtils.ip();
        this.device = WebUtils.device();
        this.loginDate = new Date();
        this.loginDay = new Date();
    }

    public String getCountry() {
        return WebUtils.country(this.ip);
    }
}
