package spoon.casino.evo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import spoon.member.entity.Member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@ToString
@Getter
@Entity
@Table(name = "CASINO_EVO_USER")
public class CasinoEvoUser {

    @Id
    @Column(length = 64, nullable = false, unique = true)
    private String userid;

    @Column(length = 64, nullable = false, unique = true)
    private String casinoId;

    private boolean success;

    public static CasinoEvoUser create(Member member) {
        CasinoEvoUser user = new CasinoEvoUser();
        user.userid = member.getUserid();
        user.casinoId = member.getCasinoEvoId();
        user.success = true;

        return user;
    }
}
