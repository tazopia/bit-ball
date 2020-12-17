package spoon.gameZone.snail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.bot.support.ZoneHelper;
import spoon.common.utils.DateUtils;
import spoon.game.domain.MenuCode;
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
@Table(name = "ZONE_SNAIL", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Snail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    @Column(length = 16)
    private String result;

    @Column(length = 16)
    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[3];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[3];

    private boolean closing;

    private boolean cancel;

    //---------------------------------------------------------

    public Snail(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    public Zone getZone(String gameCode) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.SNAIL);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%03d회차 달팽이", this.round));
        zone.setTeamHome(String.format("%03d회차 달팽이 [네팽이]", this.round));
        zone.setTeamAway(String.format("%03d회차 달팽이 [드팽이]", this.round));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getSnail().getOdds()[0]);
        zone.setOddsDraw(ZoneConfig.getSnail().getOdds()[1]);
        zone.setOddsAway(ZoneConfig.getSnail().getOdds()[2]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public ZoneScore getGameResult() {
        return ZoneHelper.zoneResult(getResult1());
    }

    public String getResult1() {
        if (this.result == null || this.result.length() != 8) {
            return "";
        }
        return result.substring(0, 2);
    }

    public String getResult2() {
        if (this.result == null || this.result.length() != 8) {
            return "";
        }
        return result.substring(3, 5);
    }

    public String getResult3() {
        if (this.result == null || this.result.length() != 8) {
            return "";
        }
        return result.substring(6, 8);
    }

    public boolean isChangeResult(SnailDto.Score score) {
        return !this.closing || this.cancel != score.isCancel() || !score.getResult().equals(this.result);
    }

    public void updateScore(SnailDto.Score score) {
        if (score.isCancel()) {
            this.result = "";
            this.cancel = true;
        } else {
            this.result = score.getResult();
            this.cancel = false;
        }
        this.closing = true;
    }
}
