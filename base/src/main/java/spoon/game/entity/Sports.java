package spoon.game.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "SPORTS", indexes = {
        @Index(name = "IDX_sports", columnList = "sportsName", unique = true)
})
public class Sports {

    public Sports(String sportsName) {
        this.sportsName = sportsName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(64)", unique = true)
    private String sportsName;

    private String sportsFlag = "sports.png";

    private boolean hidden = true;

    private int sort;

    public void sortUp() {
        --this.sort;
    }

    public void sortDown() {
        ++this.sort;
    }

}
