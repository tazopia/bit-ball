package spoon.casino.evo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;
import spoon.member.entity.Member;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@ToString
@Data
@Entity
@Table(name = "CASINO_EVO_EXCHANGE")
public class CasinoEvoExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(length = 64)
    private String userid;

    @Nationalized
    @Column(length = 64)
    private String nickname;

    private int level;

    @Column(length = 64)
    private String casinoId;

    @Column(length = 32)
    private String type;

    @Column(length = 64)
    private String gameType;

    @Column(length = 64)
    private String round;

    private long amount;

    private String trans;

    private boolean cancel;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date regDate;

    public CasinoEvoExchange(Member member) {
        this.userid = member.getUserid();
        this.nickname = member.getNickname();
        this.level = member.getLevel();
        this.regDate = new Date();
    }
}
