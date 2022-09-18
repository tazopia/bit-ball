package spoon.casino.evolution.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;
import org.springframework.transaction.annotation.Transactional;
import spoon.casino.evolution.domain.CasinoEvolutionBetDto;
import spoon.common.utils.StringUtils;
import spoon.member.domain.Role;
import spoon.member.entity.Member;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor
@ToString
@Getter
@Entity
@Table(name = "CASINO_EVOLUTION_BET")
public class CasinoEvolutionBet {

    @Id
    private Long id;

    @Nationalized
    @Column(length = 64, nullable = false)
    private String userid;

    @Nationalized
    @Column(length = 64, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Role role;

    private int level;

    @Nationalized
    @Column(length = 64)
    private String agency1;

    @Nationalized
    @Column(length = 64)
    private String agency2;

    @Nationalized
    @Column(length = 64)
    private String agency3;

    @Nationalized
    @Column(length = 64)
    private String agency4;

    @Nationalized
    @Column(length = 64)
    private String recommender;

    private String casinoId;

    private String type;

    private long betMoney;

    private long winMoney;

    private String status;

    private String gameId;

    private String gameType;

    private String round;

    private String title;

    private String vendor;

    private LocalDateTime regDate;

    private LocalDate regDay;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date regDate2;

    @Temporal(value = TemporalType.DATE)
    private Date regDay2;

    public static CasinoEvolutionBet bet(CasinoEvolutionBetDto.Bet transaction, Member member) {
        CasinoEvolutionBet bet = new CasinoEvolutionBet();
        bet.id = transaction.getId();
        bet.userid = member.getUserid();
        bet.nickname = member.getNickname();
        bet.role = member.getRole();
        bet.level = member.getLevel();
        bet.agency1 = member.getAgency1();
        bet.agency2 = member.getAgency2();
        bet.agency3 = member.getAgency3();
        bet.agency4 = member.getAgency4();
        bet.recommender = member.getRecommender();
        bet.casinoId = member.getCasinoEvolutionId();

        bet.type = transaction.getType();
        bet.betMoney = -transaction.getAmount();
        bet.winMoney = 0;
        bet.status = "bet";
        bet.gameId = transaction.getDetails().getGame().getId();
        bet.gameType = transaction.getDetails().getGame().getType();
        bet.round = transaction.getDetails().getGame().getRound();
        bet.title = transaction.getDetails().getGame().getTitle();
        bet.vendor = transaction.getDetails().getGame().getVendor();
        bet.regDate = transaction.getRegDate();
        bet.regDay = transaction.getRegDate().toLocalDate();
        bet.regDate2 = Date.from(transaction.getRegDate().atZone(ZoneId.systemDefault()).toInstant());
        bet.regDay2 = bet.regDate2;

        return bet;
    }

    public static CasinoEvolutionBet win(CasinoEvolutionBetDto.Bet transaction, Member member) {
        CasinoEvolutionBet bet = new CasinoEvolutionBet();
        bet.id = transaction.getId();
        bet.userid = member.getUserid();
        bet.nickname = member.getNickname();
        bet.role = member.getRole();
        bet.level = member.getLevel();
        bet.agency1 = member.getAgency1();
        bet.agency2 = member.getAgency2();
        bet.recommender = member.getRecommender();
        bet.casinoId = member.getCasinoEvolutionId();

        bet.type = transaction.getType();
        bet.betMoney = 0;
        bet.winMoney = transaction.getAmount();
        bet.status = "win";
        bet.gameId = transaction.getDetails().getGame().getId();
        bet.gameType = transaction.getDetails().getGame().getType();
        bet.round = transaction.getDetails().getGame().getRound();
        bet.title = transaction.getDetails().getGame().getTitle();
        bet.vendor = transaction.getDetails().getGame().getVendor();
        bet.regDate = transaction.getRegDate();
        bet.regDay = transaction.getRegDate().toLocalDate();

        bet.regDate2 = Date.from(transaction.getRegDate().atZone(ZoneId.systemDefault()).toInstant());
        bet.regDay2 = bet.regDate2;

        return bet;
    }

}
