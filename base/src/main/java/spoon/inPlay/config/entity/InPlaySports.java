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
@Table(name = "INPLAY_SPORTS")
public class InPlaySports {
    @Id
    @Column(length = 65)
    private String name;

    @Nationalized
    @Column(length = 65)
    private String korName;

    private String flag;

    public InPlaySports(String sports, String flag) {
        this.name = sports;
        this.korName = sports;
        this.flag = flag;
    }

    public void updateFlag(String saveFileName) {
        this.flag = saveFileName;
    }

    public void updateKorName(String korName) {
        this.korName = korName;
    }
}
