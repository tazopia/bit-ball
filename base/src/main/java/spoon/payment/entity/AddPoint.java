package spoon.payment.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.common.utils.WebUtils;
import spoon.payment.domain.PointCode;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "ADD_POINT")
public class AddPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    private String userid;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private PointCode pointCode;

    private long actionId;

    private long amount;

    @Column(columnDefinition = "nvarchar(1024)")
    private String memo;

    @Column(length = 64)
    private String ip;

    @Column(columnDefinition = "nvarchar(64)")
    private String worker;

    //--------------------------------------------------------------------------

    public AddPoint(String userid, PointCode pointCode, Long actionId, long amount, String memo) {
        this.userid = userid;
        this.pointCode = pointCode;
        this.actionId = actionId;
        this.amount = amount;
        this.memo = memo;
        this.ip = WebUtils.ip() == null ? "AUTO_BOT" : WebUtils.ip();
        this.worker = WebUtils.userid() == null ? "AUTO_BOT" : WebUtils.userid();
    }

    public String getCodeString() {
        return this.pointCode.name();
    }

    public String getRollbackCode() {
        return this.pointCode.name().replaceAll("_ROLLBACK", "");
    }

}
