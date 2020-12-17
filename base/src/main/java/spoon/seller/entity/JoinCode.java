package spoon.seller.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "JOIN_CODE")
public class JoinCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(64)", unique = true)
    private String code;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency1;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency2;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency3;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency4;

    @Column(columnDefinition = "NVARCHAR(512)")
    private String memo;

    private boolean enabled;

}
