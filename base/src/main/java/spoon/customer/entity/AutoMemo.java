package spoon.customer.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "AUTO_MEMO")
public class AutoMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String code;

    // 버튼 이름으로 표시될 항목
    @Column(columnDefinition = "NVARCHAR(256)")
    private String name;

    @Column(columnDefinition = "NVARCHAR(1024)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String contents;

    private boolean enabled;

}
