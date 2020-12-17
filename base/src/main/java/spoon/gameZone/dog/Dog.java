package spoon.gameZone.dog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import spoon.game.domain.GameResult;
import spoon.game.domain.MenuCode;
import spoon.gameZone.GameZoneException;
import spoon.gameZone.Zone;
import spoon.gameZone.ZoneScore;
import spoon.support.convert.LongArrayConvert;

import javax.persistence.*;
import java.time.zone.ZoneRulesException;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
@Table(name = "ZONE_DOG", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String league;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    @Column(columnDefinition = "nvarchar(256)")
    private String team1;

    @Column(columnDefinition = "nvarchar(256)")
    private String team2;

    @Column(columnDefinition = "nvarchar(256)")
    private String team3;

    @Column(columnDefinition = "nvarchar(256)")
    private String team4;

    @Column(columnDefinition = "nvarchar(256)")
    private String team5;

    @Column(columnDefinition = "nvarchar(256)")
    private String team6;

    private double odds1;

    private double odds2;

    private double odds3;

    private double odds4;

    private double odds5;

    private double odds6;

    private Integer winNumber;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[6];

    private String sdate;

    private boolean closing;

    private boolean cancel;

    private String dog1;

    private String dog2;

    private String dog3;

    //-----------------------------------------------------------

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    public void updateScore(DogDto.Score score) {
        if (score.isCancel()) {
            this.winNumber = null;
            this.dog1 = "";
            this.dog2 = "";
            this.dog3 = "";
            this.cancel = true;
        } else {
            this.winNumber = score.getWinNumber();
            switch (this.winNumber) {
                case 1:
                case 2:
                    this.dog1 = this.winNumber == 1 ? "home" : "away";
                    this.dog2 = "";
                    this.dog3 = "";
                    break;
                case 3:
                case 4:
                    this.dog1 = "";
                    this.dog2 = this.winNumber == 3 ? "home" : "away";
                    this.dog3 = "";
                    break;
                case 5:
                case 6:
                    this.dog1 = "";
                    this.dog2 = "";
                    this.dog3 = this.winNumber == 5 ? "home" : "away";
                    break;
            }
            this.cancel = false;
        }
        this.closing = true;
    }

    public ZoneScore getGameResult(String gameCode) {
        if (this.cancel) return new ZoneScore(0, 0, GameResult.CANCEL);
        switch (gameCode) {
            case "dog1":
                if (this.winNumber == 1) {
                    return new ZoneScore(1, 0, GameResult.HOME);
                } else if (this.winNumber == 2) {
                    return new ZoneScore(0, 1, GameResult.AWAY);
                } else {
                    return new ZoneScore(0, 0, GameResult.NONE);
                }
            case "dog2":
                if (this.winNumber == 3) {
                    return new ZoneScore(1, 0, GameResult.HOME);
                } else if (this.winNumber == 4) {
                    return new ZoneScore(0, 1, GameResult.AWAY);
                } else {
                    return new ZoneScore(0, 0, GameResult.NONE);
                }
            case "dog3":
                if (this.winNumber == 5) {
                    return new ZoneScore(1, 0, GameResult.HOME);
                } else if (this.winNumber == 6) {
                    return new ZoneScore(0, 1, GameResult.AWAY);
                } else {
                    return new ZoneScore(0, 0, GameResult.NONE);
                }
            default:
                throw new GameZoneException("개경주 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "dog1":
                return getDogZone("dog1", "1. " + team1, "2. " + team2, odds1, odds2);
            case "dog2":
                return getDogZone("dog2", "3. " + team3, "4. " + team4, odds3, odds4);
            case "dog3":
                return getDogZone("dog3", "5. " + team5, "6. " + team6, odds5, odds6);
            default:
                throw new GameZoneException("개경주 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getDogZone(String gameCode, String teamHome, String teamAway, double oddsHome, double oddsAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.DOG);
        zone.setGameCode(gameCode);
        zone.setLeague(this.league);
        zone.setTeamHome(teamHome);
        zone.setTeamAway(teamAway);
        zone.setHandicap(0);
        zone.setOddsHome(oddsHome);
        zone.setOddsDraw(0);
        zone.setOddsAway(oddsAway);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(DogDto.Score score) {
        return !this.closing || this.cancel != score.isCancel() || this.winNumber != score.getWinNumber();
    }

    public double getOdds(int betZone) {
        switch (betZone) {
            case 0:
                return odds1;
            case 1:
                return odds2;
            case 2:
                return odds3;
            case 3:
                return odds4;
            case 4:
                return odds5;
            case 5:
                return odds6;
            default:
                throw new ZoneRulesException("개경주 배당 정보를 가져올 수 없습니다. - betZone : " + betZone);
        }
    }
}
