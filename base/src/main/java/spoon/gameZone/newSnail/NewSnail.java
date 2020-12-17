package spoon.gameZone.newSnail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.bot.support.ZoneHelper;
import spoon.common.utils.DateUtils;
import spoon.game.domain.GameResult;
import spoon.game.domain.MenuCode;
import spoon.gameZone.GameZoneException;
import spoon.gameZone.Zone;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneScore;
import spoon.support.convert.DoubleArrayConvert;
import spoon.support.convert.LongArrayConvert;

import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
@Entity
@Table(name = "ZONE_NEW_SNAIL", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class NewSnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    @Column(length = 16)
    private String ranking;

    @Column(length = 16)
    private String oe;

    @Column(length = 16)
    private String ou;

    @Column(length = 16)
    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[8];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[8];

    private boolean closing;

    private boolean cancel;

    //---------------------------------------------------------

    public NewSnail(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "oddeven":
                return getNewSnailZone("oddeven", "홀", "짝", 0, 1);
            case "overunder":
                return getNewSnailZone("overunder", "오버", "언더", 2, 3);
            case "line1":
                return getNewSnailZone("line1", "1", "2", 4, 5);
            case "line2":
                return getNewSnailZone("line2", "3", "4", 6, 7);
            default:
                throw new GameZoneException("NEW 달팽이 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public Zone getNewSnailZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.NEW_SNAIL);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%03d회차 NEW 달팽이", this.round));
        zone.setTeamHome(String.format("%03d회차 NEW 달팽이 [%s]", this.round, teamHome));
        zone.setTeamAway(String.format("%03d회차 NEW 달팽이 [%s]", this.round, teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getNewSnail().getOdds()[idxHome]);
        zone.setOddsDraw(0);
        zone.setOddsAway(ZoneConfig.getNewSnail().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public ZoneScore getGameResult(String gameCode) {
        if (this.cancel) {
            return new ZoneScore(0, 0, GameResult.CANCEL);
        }
        switch (gameCode) {
            case "oddeven":
                return ZoneHelper.zoneResult(oe);
            case "overunder":
                return ZoneHelper.zoneResult(ou);
            case "line1":
                if ("1".equals(this.ranking)) {
                    return new ZoneScore(1, 0, GameResult.HOME);
                } else if ("2".equals(this.ranking)) {
                    return new ZoneScore(0, 1, GameResult.AWAY);
                } else {
                    return new ZoneScore(0, 0, GameResult.NONE);
                }
            case "line2":
                if ("3".equals(this.ranking)) {
                    return new ZoneScore(1, 0, GameResult.HOME);
                } else if ("4".equals(this.ranking)) {
                    return new ZoneScore(0, 1, GameResult.AWAY);
                } else {
                    return new ZoneScore(0, 0, GameResult.NONE);
                }
            default:
                throw new GameZoneException("NEW 달팽이 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public boolean isChangeResult(NewSnailDto.Score score) {
        return !this.closing || this.cancel != score.isCancel() || !score.getRanking().equals(this.ranking);
    }

    public void updateScore(NewSnailDto.Score score) {
        if (score.isCancel()) {
            this.ranking = "";
            this.oe = "";
            this.ou = "";
            this.cancel = true;
        } else {
            this.ranking = score.getRanking();
            this.cancel = false;
            switch (this.ranking) {
                case "1":
                    this.oe = "odd";
                    this.ou = "under";
                    break;
                case "2":
                    this.oe = "even";
                    this.ou = "under";
                    break;
                case "3":
                    this.oe = "odd";
                    this.ou = "over";
                    break;
                case "4":
                    this.oe = "even";
                    this.ou = "over";
                    break;
            }
        }
        this.closing = true;
    }
}
