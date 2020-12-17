package spoon.board.entity;

import lombok.Data;
import spoon.bet.entity.Bet;
import spoon.member.domain.Role;
import spoon.support.convert.LongArrayConvert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Data
@Entity
@Table(name = "BOARD")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false)
    private String code;

    @Column(columnDefinition = "NVARCHAR(64)", nullable = false)
    private String userid;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Role role;

    private int level;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency1;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency2;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency3;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency4;

    @Convert(converter = LongArrayConvert.class)
    private long[] betId;

    private boolean bet;

    @Column(columnDefinition = "NVARCHAR(1024)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String contents;

    private int hit;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    private long comment;

    private boolean enabled;

    private boolean hidden;

    private boolean showTop;

    private boolean alarm = true;

    @Transient
    private Iterable<Comment> comments = new ArrayList<>();

    @Transient
    private Iterable<Bet> bets = new ArrayList<>();

    public Long[] betIds() {
        Long[] ids = new Long[this.betId.length];
        int index = 0;
        for (long id : this.betId) {
            ids[index++] = id;
        }
        return ids;
    }

    public boolean isNew() {
        return new Date().getTime() - this.regDate.getTime() < 1000 * 60 * 60 * 24; // 하루
    }

    public void addHit() {
        this.hit++;
    }
}
