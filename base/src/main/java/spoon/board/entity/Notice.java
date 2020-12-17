package spoon.board.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "NOTICE")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(1024)")
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    private boolean enabled;

}
