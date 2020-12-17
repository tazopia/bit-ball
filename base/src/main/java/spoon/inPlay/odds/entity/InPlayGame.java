package spoon.inPlay.odds.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.entity.InPlayLeague;
import spoon.inPlay.config.entity.InPlaySports;
import spoon.inPlay.config.entity.InPlayTeam;

import javax.persistence.*;
import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "INPLAY_GAME")
public class InPlayGame {

    @JsonProperty("FixtureId")
    @Id
    private Long fixtureId;

    @Nationalized
    @Column(length = 64)
    private String sname;

    @Nationalized
    @Column(length = 128)
    private String location;

    @Nationalized
    private String league;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sdate;

    @Nationalized
    private String hname;

    @Nationalized
    private String aname;

    @JsonProperty("sts")
    private int status;

    @JsonIgnore
    private long ut;

    @Setter
    @Transient
    private Long odds;

    @PrePersist
    @PreUpdate
    public void update() {
        ut = System.currentTimeMillis();
    }

    public void updateStatus(int status) {
        this.status = status;
    }
}
