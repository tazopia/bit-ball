package spoon.ip.entity;

import lombok.Data;
import spoon.common.utils.WebUtils;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "IP_ADDR", indexes = {
        @Index(name = "IDX_ip", columnList = "ip")
})
public class IpAddr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32)
    private String code;

    @Column(length = 64)
    private String ip;

    @Column(length = 1024)
    private String memo;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date regDate;

    @Column(columnDefinition = "nvarchar(64)")
    private String worker;

    @PrePersist
    public void prePersist() {
        this.regDate = new Date();
        this.worker = WebUtils.userid();
    }
}
