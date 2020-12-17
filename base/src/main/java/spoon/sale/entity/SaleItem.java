package spoon.sale.entity;

import lombok.Data;
import spoon.member.domain.Role;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "SALE_ITEM", indexes = {
        @Index(name = "IDX_userid", columnList = "userid")
})
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "saleId", insertable = false, updatable = false)
    private Sale sale;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String userid;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency2;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency1;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency3;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency4;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private Role role;

    private long inMoney;

    private long outMoney;

    private long betSports;

    private long betZone;

    private long users;

    @Column(length = 16)
    private String rateCode;

    private double rateShare;

    private double rateSports;

    private double rateZone;

    private long lastMoney;

    private long totalMoney;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    private boolean closing;

    public long getCalcMoney() {
        if ("수익분배".equals(this.rateCode)) {
            return (long) ((this.inMoney - this.outMoney) * this.rateShare / 100D);
        } else if ("롤링분배".equals(this.rateCode)) {
            return (long) (this.betSports * this.rateSports / 100D + this.betZone * this.rateZone / 100D);
        } else {
            return 0;
        }
    }

    public int getHq() {
        if (this.role.ordinal() >= Role.AGENCY4.ordinal()) return 4;
        if (this.role.ordinal() >= Role.AGENCY3.ordinal()) return 3;
        if (this.role.ordinal() >= Role.AGENCY2.ordinal()) return 2;
        return 1;
    }
}
