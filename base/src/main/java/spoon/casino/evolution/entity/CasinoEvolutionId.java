package spoon.casino.evolution.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@ToString
@Getter
@Entity
@Table(name = "CASINO_EVOLUTION_ID")
public class CasinoEvolutionId {

    @Id
    @Column(length = 64, nullable = false, unique = true)
    private String userid;

    @Column(length = 64, nullable = false, unique = true)
    private String casinoid;

    private CasinoEvolutionId(String userid, String casinoid) {
        this.userid = userid;
        this.casinoid = casinoid;
    }

    public static CasinoEvolutionId of(String userid, String casinoid) {
        return new CasinoEvolutionId(userid, casinoid);
    }
}
