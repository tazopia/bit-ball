package spoon.sale.entity;

import lombok.Data;
import spoon.member.domain.Role;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "SALE")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date regDate;

    private boolean closing;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "saleId")
    private List<SaleItem> saleItems;
}
