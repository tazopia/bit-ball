package spoon.payment.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.common.utils.WebUtils;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.payment.domain.PointCode;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "POINT", indexes = {
        @Index(name = "IDX_userid", columnList = "userid"),
        @Index(name = "IDX_agency1", columnList = "agency1"),
        @Index(name = "IDX_agency2", columnList = "agency2"),
        @Index(name = "IDX_agency3", columnList = "agency3"),
        @Index(name = "IDX_agency4", columnList = "agency4"),
        @Index(name = "IDX_pointCode", columnList = "pointCode"),
        @Index(name = "IDX_regDate", columnList = "regDate")
})
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    private String userid;

    @Column(columnDefinition = "nvarchar(64)")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
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

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private PointCode pointCode;

    private long actionId;

    private long orgPoint;

    private long amount;

    private boolean cancel;

    @Column(columnDefinition = "nvarchar(1024)")
    private String memo;

    @Column(length = 64)
    private String ip;

    @Column(columnDefinition = "nvarchar(64)")
    private String worker;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    //--------------------------------------------------------------------------

    public Point(User user, PointCode pointCode, Long actionId, long orgPoint, long amount, String memo) {
        this.userid = user.getUserid();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.level = user.getLevel();
        this.agency1 = user.getAgency1();
        this.agency2 = user.getAgency2();
        this.agency3 = user.getAgency3();
        this.agency4 = user.getAgency4();
        this.pointCode = pointCode;
        this.actionId = actionId;
        this.orgPoint = orgPoint;
        this.amount = amount;
        this.memo = memo;
        this.regDate = new Date();
        this.ip = WebUtils.ip() == null ? "AUTO_BOT" : WebUtils.ip();
        this.worker = WebUtils.userid() == null ? "AUTO_BOT" : WebUtils.userid();
    }

    public long getEndPoint() {
        return this.orgPoint - this.amount;
    }
}
