package spoon.gameZone.keno.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.bot.support.ZoneHelper;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;
import spoon.game.domain.MenuCode;
import spoon.gameZone.GameZoneException;
import spoon.gameZone.Zone;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneScore;
import spoon.gameZone.keno.domain.KenoDto;
import spoon.support.convert.DoubleArrayConvert;
import spoon.support.convert.LongArrayConvert;

import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
@Entity
@Table(name = "ZONE_KENO", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Keno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    // 합계
    private int sum;

    // 오버언더
    @Column(length = 16)
    private String overunder;

    // 홀짝
    @Column(length = 16)
    private String oddeven;

    @Column(length = 16)
    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[4];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[4];

    private boolean closing;

    private boolean cancel;

    //--------------------------------------------------------

    public Keno(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    // 스코어 입력
    public void updateScore(KenoDto.Score score) {
        if (StringUtils.empty(score.getOddeven()) || StringUtils.empty(score.getOverunder())) {
            this.oddeven = "";
            this.overunder = "";
            this.cancel = true;
        } else {
            this.oddeven = score.getOddeven();
            this.overunder = score.getOverunder();
            this.sum = score.getSum();
            this.cancel = false;
        }
        this.closing = true;
    }

    // 경기 결과
    public ZoneScore getGameResult(String gameCode) {
        switch (gameCode) {
            case "oddeven":
                return ZoneHelper.zoneResult(oddeven);
            case "overunder":
                return ZoneHelper.zoneResult(overunder);
            default:
                throw new GameZoneException("키노 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "oddeven":
                return getKenoZone("oddeven", "홀", "짝", 0, 1);
            case "overunder":
                return getKenoZone("overunder", "오버", "언더", 2, 3);
            default:
                throw new GameZoneException("키노 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getKenoZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.KENO);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%03d회차 스피드키노", this.round));
        zone.setTeamHome(String.format("%03d회차 스피드키노 [%s]", this.round, teamHome));
        zone.setTeamAway(String.format("%03d회차 스피드키노 [%s]", this.round, teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getKeno().getOdds()[idxHome]);
        zone.setOddsDraw(0);
        zone.setOddsAway(ZoneConfig.getKeno().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(KenoDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(score.getOddeven().equals(this.oddeven) && score.getOverunder().equals(this.overunder));

    }
}
