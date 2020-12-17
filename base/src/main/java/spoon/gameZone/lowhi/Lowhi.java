package spoon.gameZone.lowhi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.bot.support.ZoneHelper;
import spoon.common.utils.DateUtils;
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
@Table(name = "ZONE_LOWHI", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Lowhi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    @Column(length = 16)
    private String lowhi;

    @Column(length = 16)
    private String oddeven;

    @Column(length = 16)
    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[8];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[8];

    private boolean closing;

    private boolean cancel;

    //---------------------------------------------------------------------------

    public Lowhi(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    // 스코어 입력
    public void updateScore(LowhiDto.Score score) {
        if (score.isCancel()) {
            this.oddeven = "";
            this.lowhi = "";
            this.cancel = true;
        } else {
            this.oddeven = score.getOddeven();
            this.lowhi = score.getLowhi();
            this.cancel = false;
        }
        this.closing = true;
    }

    // 경기 결과
    public ZoneScore getGameResult(String gameCode) {
        switch (gameCode) {
            case "oddeven":
                return ZoneHelper.zoneResult(oddeven);
            case "lowhi":
                return ZoneHelper.zoneResult(lowhi);
            case "lowOddeven":
                return ZoneHelper.lowOddevenResult(lowhi, oddeven);
            case "hiOddeven":
                return ZoneHelper.hiOddevenResult(lowhi, oddeven);
            default:
                throw new GameZoneException("로하이 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "oddeven":
                return getLadderZone("oddeven", "홀", "짝", 0, 1);
            case "lowhi":
                return getLadderZone("lowhi", "로우", "하이", 2, 3);
            case "lowOddeven":
                return getLadderZone("lowOddeven", "로우/홀", "로우/짝", 4, 5);
            case "hiOddeven":
                return getLadderZone("hiOddeven", "하이/홀", "하이/짝", 6, 7);
            default:
                throw new GameZoneException("로하이 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getLadderZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.LOWHI);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%03d회차 로하이", this.round));
        zone.setTeamHome(String.format("%03d회차 로하이 [%s]", this.round, teamHome));
        zone.setTeamAway(String.format("%03d회차 로하이 [%s]", this.round, teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getLowhi().getOdds()[idxHome]);
        zone.setOddsDraw(0);
        zone.setOddsAway(ZoneConfig.getLowhi().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(LowhiDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(score.getOddeven().equals(this.oddeven) && score.getLowhi().equals(this.lowhi));

    }
}
