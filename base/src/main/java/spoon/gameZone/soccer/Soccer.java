package spoon.gameZone.soccer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import spoon.bot.support.ZoneHelper;
import spoon.game.domain.MenuCode;
import spoon.gameZone.GameZoneException;
import spoon.gameZone.Zone;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneScore;
import spoon.support.convert.LongArrayConvert;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.zone.ZoneRulesException;
import java.util.Date;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
@Table(name = "ZONE_SOCCER", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Soccer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(length = 64)
    private String league;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    @Column(columnDefinition = "nvarchar(256)")
    private String teamHome;

    @Column(columnDefinition = "nvarchar(256)")
    private String teamAway;

    private double maHome; // 0

    private double maDraw; // 1

    private double maAway; // 2

    private double ahHome; // 3

    private double ahDraw; // ah handy

    private double ahAway; // 4

    private double ouHome; // 5

    private double ouDraw; // ou handy

    private double ouAway; // 6

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[7];

    private int scoreHome;

    private int scoreAway;

    @Column(length = 12)
    private String sdate;

    private boolean closing;

    private boolean cancel;

    private String ma;

    private String ah;

    private String ou;

    public double getMaHomeOdds() {
        return odds(this.maHome);
    }

    public double getMaDrawOdds() {
        return odds(this.maDraw);
    }

    public double getMaAwayOdds() {
        return odds(this.maAway);
    }

    public double getAhHomeOdds() {
        return odds(this.ahHome);
    }

    public double getAhAwayOdds() {
        return odds(this.ahAway);
    }

    public double getOuHomeOdds() {
        return odds(this.ouHome);
    }

    public double getOuAwayOdds() {
        return odds(this.ouAway);
    }

    private double odds(double odds) {
        double oddsRate = ZoneConfig.getSoccer().getOdds();
        double oddsPlus = ZoneConfig.getSoccer().getOddsUpDown();

        if (oddsPlus != 100D) {
            odds = BigDecimal.valueOf(odds)
                    .multiply(BigDecimal.valueOf(oddsPlus))
                    .divide(BigDecimal.valueOf(100D), 2, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
        }

        if (oddsRate != 0D) {
            odds = BigDecimal.valueOf(odds).add(BigDecimal.valueOf(oddsRate)).doubleValue();
        }

        if (odds < ZoneConfig.getSoccer().getMinOdds()) {
            odds = ZoneConfig.getSoccer().getMinOdds();
        }

        if (odds > ZoneConfig.getSoccer().getMaxOdds()) {
            odds = ZoneConfig.getSoccer().getMaxOdds();
        }

        return odds;
    }

    //-----------------------------------------------------------------

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    public void updateScore(SoccerDto.Score score) {
        if (score.isCancel()) {
            this.scoreHome = 0;
            this.scoreAway = 0;
            this.ma = "";
            this.ah = "";
            this.ou = "";
            this.cancel = true;
        } else {
            this.scoreHome = score.getScoreHome();
            this.scoreAway = score.getScoreAway();
            double result = this.scoreHome - this.scoreAway;
            if (result > 0) {
                this.ma = "home";
            } else if (result < 0) {
                this.ma = "away";
            } else {
                this.ma = "draw";
            }
            result = result + this.ahDraw;
            if (result > 0) {
                this.ah = "home";
            } else if (result < 0) {
                this.ah = "away";
            } else {
                this.ah = "draw";
            }
            result = this.scoreHome + this.scoreAway - this.ouDraw;
            if (result > 0) {
                this.ou = "home";
            } else if (result < 0) {
                this.ou = "away";
            } else {
                this.ou = "draw";
            }

            this.cancel = false;
        }
        this.closing = true;
    }

    public ZoneScore getGameResult(String gameCode) {
        switch (gameCode) {
            case "승무패":
                return ZoneHelper.maResult(scoreHome, scoreAway, cancel);
            case "핸디캡":
                return ZoneHelper.ahResult(scoreHome, scoreAway, ahDraw, cancel);
            case "오버언더":
                return ZoneHelper.ouResult(scoreHome, scoreAway, ouDraw, cancel);
            default:
                throw new GameZoneException("가상축구 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "승무패":
                return getSoccerZone("승무패", 0, getMaHomeOdds(), getMaDrawOdds(), getMaAwayOdds());
            case "핸디캡":
                return getSoccerZone("핸디캡", ahDraw, getAhHomeOdds(), 0, getAhAwayOdds());
            case "오버언더":
                return getSoccerZone("오버언더", ouDraw, getOuHomeOdds(), 0, getOuAwayOdds());
            default:
                throw new GameZoneException("가상축구 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getSoccerZone(String gameCode, double handy, double oddsHome, double oddsDraw, double oddsAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.SOCCER);
        zone.setGameCode(gameCode);
        zone.setLeague(this.league);
        zone.setTeamHome(this.teamHome);
        zone.setTeamAway(this.teamAway);
        zone.setHandicap(handy);
        zone.setOddsHome(oddsHome);
        zone.setOddsDraw(oddsDraw);
        zone.setOddsAway(oddsAway);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(SoccerDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(score.getScoreHome().equals(this.scoreHome) && score.getScoreAway().equals(this.scoreAway));

    }

    public String getLeagueName() {
        switch (this.league) {
            case "Superleague":
                return "슈퍼리그";
            case "Premiership":
                return "프리미어쉽";
            case "World Cup":
                return "월드컵";
            default:
                return "";
        }
    }

    public double getOdds(int betZone) {
        switch (betZone) {
            case 0:
                return getMaHomeOdds();
            case 1:
                return getMaDrawOdds();
            case 2:
                return getMaAwayOdds();
            case 3:
                return getAhHomeOdds();
            case 4:
                return getAhAwayOdds();
            case 5:
                return getOuHomeOdds();
            case 6:
                return getOuAwayOdds();
            default:
                throw new ZoneRulesException("가상축구 배당 정보를 가져올 수 없습니다. - betZone : " + betZone);
        }
    }

}
