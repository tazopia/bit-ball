package spoon.payment.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.member.domain.Role;
import spoon.payment.domain.MoneyCode;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "MONEY", indexes = {
        @Index(name = "IDX_userid", columnList = "userid"),
        @Index(name = "IDX_agency1", columnList = "agency1"),
        @Index(name = "IDX_agency2", columnList = "agency2"),
        @Index(name = "IDX_agency3", columnList = "agency3"),
        @Index(name = "IDX_agency4", columnList = "agency4"),
        @Index(name = "IDX_moneyCode", columnList = "moneyCode"),
        @Index(name = "IDX_regDate", columnList = "regDate")
})
public class Money {

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
    private MoneyCode moneyCode;

    private Long actionId;

    private long orgMoney;

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

    //----------------------------------------------------------------------

    public long getEndMoney() {
        return this.orgMoney + this.amount;
    }
}
