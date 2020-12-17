package spoon.event.entity;

import lombok.Data;
import spoon.support.convert.CryptoConverter;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "LOTTO_PAYMENT")
public class LottoPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String memo;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String userid;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String nickname;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency1;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency2;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency3;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency4;

    private long amount;

    @Column(length = 16)
    private String sdate;

    private boolean deleted;

    private Date regDate;

    // 출금시
    @Column(length = 64)
    private String bank;

    @Convert(converter = CryptoConverter.class)
    private String account;

    @Convert(converter = CryptoConverter.class)
    private String depositor;

}
