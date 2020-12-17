package spoon.customer.entity;

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
@Table(name = "MEMO", indexes = {
        @Index(name = "IDX_userid", columnList = "userid")
})
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(64)", nullable = false)
    private String userid;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Role role;

    private int level;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency1;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency2;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency3;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency4;

    @Column(columnDefinition = "NVARCHAR(1024)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(2048)")
    private String contents;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    // 사용자 확인 유무
    private boolean checked;

    // 사용자 삭제 유무
    private boolean hidden;

    // 관리자 삭제 유무
    private boolean enabled = true;

    @Column(length = 64)
    private String ip;

    @Column(columnDefinition = "VARCHAR(64)")
    private String worker;

    public Memo(User user) {
        this.userid = user.getUserid();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.level = user.getLevel();
        this.agency1 = user.getAgency1();
        this.agency2 = user.getAgency2();
        this.agency3 = user.getAgency3();
        this.agency4 = user.getAgency4();
        this.ip = WebUtils.ip();
        this.regDate = new Date();
        this.worker = WebUtils.userid();
    }
}
