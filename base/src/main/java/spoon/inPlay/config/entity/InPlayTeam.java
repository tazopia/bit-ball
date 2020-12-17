package spoon.inPlay.config.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "INPLAY_TEAM")
public class InPlayTeam {

    @Id
    @Nationalized
    private String name;

    @Column(length = 65)
    private String sports;

    private String league;

    @Nationalized
    private String korName;

    public InPlayTeam(String team, String sports, String league) {
        this.name = team;
        this.korName = team;
        this.sports = sports;
        this.league = league;
    }

    public void updateKorName(String korName) {
        this.korName = korName;
    }
}
