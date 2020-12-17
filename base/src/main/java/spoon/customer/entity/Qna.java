package spoon.customer.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.bet.entity.Bet;
import spoon.common.utils.WebUtils;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.support.convert.LongArrayConvert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "QNA", indexes = {
        @Index(name = "IDX_userid", columnList = "userid")
})
public class Qna {

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

    @Convert(converter = LongArrayConvert.class)
    private long[] betId;

    private boolean bet;

    @Column(columnDefinition = "NVARCHAR(512)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(2048)")
    private String contents;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    @Column(columnDefinition = "NVARCHAR(512)")
    private String reTitle;

    @Column(columnDefinition = "NVARCHAR(2048)")
    private String reply;

    @Temporal(TemporalType.TIMESTAMP)
    private Date reDate;

    // 답글 유무
    private boolean re;

    // 사용자 확인 유무
    private boolean checked;

    // 사용자 삭제 유무
    private boolean hidden;

    // 관리자 삭제 유무
    private boolean enabled = true;

    // 알람 울림 유무
    private boolean alarm = true;

    @Column(length = 64)
    private String ip;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String worker;

    @Transient
    private Iterable<Bet> bets = new ArrayList<>();

    public Long[] betIds() {
        Long[] ids = new Long[this.betId.length];
        int index = 0;
        for (long id : this.betId) {
            ids[index++] = id;
        }
        return ids;
    }

    public Qna(User user) {
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
    }
}
