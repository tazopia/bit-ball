package spoon.inPlay.config.entity;

import lombok.AllArgsConstructor;
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
@Table(name = "INPLAY_LEAGUE")
public class InPlayLeague {

    @Id
    private String name;

    @Column(length = 65)
    private String sports;

    private String location;

    @Nationalized
    private String korName;

    private String flag;

    public InPlayLeague(String league, String sports, String location, String flag) {
        this.name = league;
        this.korName = league;
        this.sports = sports;
        this.location = location;
        this.flag = flag;
    }

    public void updateFlag(String saveFileName) {
        this.flag = saveFileName;
    }

    public void updateKorName(String korName) {
        this.korName = korName;
    }
}
