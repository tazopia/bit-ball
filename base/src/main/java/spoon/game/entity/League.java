package spoon.game.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "LEAGUE", indexes = {
        @Index(name = "IDX_league", columnList = "leagueName, sports", unique = true)
})
public class League {

    public League(String sports, String leagueName) {
        this.sports = sports;
        this.leagueName = leagueName;
        this.leagueKor = leagueName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    private String sports;

    @Column(columnDefinition = "nvarchar(256)")
    private String leagueName;

    private String leagueFlag = "league.png";

    @Column(columnDefinition = "nvarchar(256)")
    private String leagueKor = "";

    private boolean enabled = true;

    public String getKey() {
        return (this.sports + "-" + this.leagueName).toLowerCase();
    }

}
