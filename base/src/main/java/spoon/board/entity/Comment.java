package spoon.board.entity;

import lombok.Data;
import spoon.member.domain.Role;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "COMMENT")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bid;

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    private String userid;

    @Column(columnDefinition = "nvarchar(64)")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Role role;

    private int level;

    @Column(columnDefinition = "nvarchar(64)")
    private String agency1;

    @Column(columnDefinition = "nvarchar(64)")
    private String agency2;

    @Column(columnDefinition = "nvarchar(64)")
    private String agency3;

    @Column(columnDefinition = "nvarchar(64)")
    private String agency4;

    @Column(columnDefinition = "nvarchar(1024)")
    private String contents;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    private boolean enabled;

    private boolean hidden;
}
