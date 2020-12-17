package spoon.game.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TEAM", indexes = {
        @Index(name = "IDX_team", columnList = "sports, teamName", unique = true)
})
public class Team {

    public Team(String sports, String teamName) {
        this.sports = sports;
        this.teamName = teamName;
        this.teamKor = teamName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(64)")
    private String sports;

    @Column(columnDefinition = "nvarchar(256)")
    private String teamName;

    @Column(columnDefinition = "nvarchar(256)")
    private String teamKor;

    public String getKey() {
        return (this.sports + "-" + this.teamName).toLowerCase();
    }
}
