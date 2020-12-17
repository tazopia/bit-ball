package spoon.gameZone.powerLadder;

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
@Table(name = "ZONE_POWER_LADDER", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class PowerLadder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    private int times;

    // 좌우
    @Column(length = 16)
    private String start;

    // 3줄,4줄
    @Column(length = 16)
    private String line;

    // 홀짝
    @Column(length = 16)
    private String oddeven;

    @Column(length = 16)
    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[10];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[10];

    private boolean closing;

    private boolean cancel;

    //--------------------------------------------------------

    public PowerLadder(int times, int round, Date gameDate) {
        this.times = times;
        this.round = round > 288 ? round % 288 : round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    // 스코어 입력
    public void updateScore(PowerLadderDto.Score score) {
        if (score.isCancel()) {
            this.oddeven = "";
            this.start = "";
            this.line = "";
            this.cancel = true;
        } else {
            this.oddeven = score.getOddeven();
            this.start = score.getStart();
            this.line = score.getLine();
            this.cancel = false;
        }
        this.closing = true;
    }

    // 경기 결과
    public ZoneScore getGameResult(String gameCode) {
        switch (gameCode) {
            case "oddeven":
                return ZoneHelper.zoneResult(oddeven);
            case "start":
                return ZoneHelper.zoneResult(start);
            case "line":
                return ZoneHelper.zoneResult(line);
            case "line3Start":
                return ZoneHelper.line3StartResult(line, start);
            case "line4Start":
                return ZoneHelper.line4StartResult(line, start);
            default:
                throw new GameZoneException("파워사다리 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "oddeven":
                return getPowerLadderZone("oddeven", "홀", "짝", 0, 1);
            case "start":
                return getPowerLadderZone("start", "좌", "우", 2, 3);
            case "line":
                return getPowerLadderZone("line", "3줄", "4줄", 4, 5);
            case "line3Start":
                return getPowerLadderZone("line3Start", "3줄/좌", "3줄/우", 6, 7);
            case "line4Start":
                return getPowerLadderZone("line4Start", "4줄/좌", "4줄/우", 8, 9);
            default:
                throw new GameZoneException("파워사다리 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getPowerLadderZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.POWER_LADDER);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%03d회차 파워사다리", this.round));
        zone.setTeamHome(String.format("%03d회차 파워사다리 [%s]", this.round, teamHome));
        zone.setTeamAway(String.format("%03d회차 파워사다리 [%s]", this.round, teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getPowerLadder().getOdds()[idxHome]);
        zone.setOddsDraw(0);
        zone.setOddsAway(ZoneConfig.getPowerLadder().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(PowerLadderDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(score.getOddeven().equals(this.oddeven) && score.getStart().equals(this.start) && score.getLine().equals(this.line));

    }
}
