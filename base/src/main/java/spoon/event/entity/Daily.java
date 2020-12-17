package spoon.event.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DAILY")
public class Daily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String userid;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String nickname;

    @Column(length = 16)
    private String sdate;

    private int daily;

}
