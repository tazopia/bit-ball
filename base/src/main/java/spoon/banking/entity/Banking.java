package spoon.banking.entity;

import lombok.Data;
import spoon.banking.domain.BankingCode;
import spoon.banking.domain.Rolling;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.member.domain.Role;
import spoon.support.convert.CryptoConverter;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "BANKING", indexes = {
        @Index(name = "IDX_userid", columnList = "userid"),
        @Index(name = "IDX_agency1", columnList = "agency1"),
        @Index(name = "IDX_agency2", columnList = "agency2"),
        @Index(name = "IDX_agency3", columnList = "agency3"),
        @Index(name = "IDX_agency4", columnList = "agency4"),
        @Index(name = "IDX_bankingCode", columnList = "bankingCode"),
        @Index(name = "IDX_regDate", columnList = "regDate")
})
public class Banking {

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
    private BankingCode bankingCode;

    @Column(length = 64)
    private String bonus;

    // 신청시 보유 머니
    private long money;

    private long point;

    private long amount;

    private long bonusPoint;

    private long fees;

    // 은행정보
    private String bank;

    @Convert(converter = CryptoConverter.class)
    private String account;

    @Convert(converter = CryptoConverter.class)
    private String depositor;

    @Column(length = 64)
    private String ip;

    @Column(columnDefinition = "nvarchar(64)")
    private String worker;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date closingDate;

    private boolean cancel;

    private boolean closing;

    private boolean hidden;

    private boolean alarm = true;

    private boolean reset;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String rolling;

    // ----------------------------------------------------------

    public String getTerm() {
        Date date = closingDate == null ? new Date() : closingDate;
        long ss = date.getTime() - regDate.getTime();

        long shour, sminute, ssecond;
        long minute = 60 * 1000;
        long hour = 60 * 60 * 1000;

        shour = ss / hour;
        sminute = (ss % hour) / minute;
        ssecond = ((ss % minute) % (60 * 1000)) / 1000;

        return String.format("%02d", shour) + ":" + String.format("%02d", sminute) + ":" + String.format("%02d", ssecond);
    }

    public String getKorean() {
        return StringUtils.num2han(amount);
    }

    public Rolling getRolling() {
        Rolling rollingMoney = JsonUtils.toModel(this.rolling, Rolling.class);
        return rollingMoney == null ? new Rolling() : rollingMoney;
    }
}
