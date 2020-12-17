package spoon.payment.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.common.utils.WebUtils;
import spoon.payment.domain.MoneyCode;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "ADD_MONEY")
public class AddMoney {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    private String userid;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private MoneyCode moneyCode;

    private Long actionId;

    private long amount;

    @Column(columnDefinition = "nvarchar(1024)")
    private String memo;

    @Column(length = 64)
    private String ip;

    @Column(columnDefinition = "nvarchar(64)")
    private String worker;

    //--------------------------------------------------------------------------

    public AddMoney(String userid, MoneyCode moneyCode, Long actionId, long amount, String memo) {
        this.userid = userid;
        this.moneyCode = moneyCode;
        this.actionId = actionId;
        this.amount = amount;
        this.memo = memo;
        this.ip = WebUtils.ip() == null ? "AUTO_BOT" : WebUtils.ip();
        this.worker = WebUtils.userid() == null ? "AUTO_BOT" : WebUtils.userid();
    }

    public String getCodeString() {
        return this.moneyCode.name();
    }

    public String getRollbackCode() {
        return this.moneyCode.name().replaceAll("_ROLLBACK", "");
    }

}
