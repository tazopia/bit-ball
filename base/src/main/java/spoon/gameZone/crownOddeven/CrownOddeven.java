package spoon.gameZone.crownOddeven;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "ZONE_CW_ODDEVEN", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class CrownOddeven {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    // 히든카드
    @JsonProperty("hidden")
    @Column(length = 4)
    private String card1;

    // 오픈카드
    @JsonProperty("open")
    @Column(length = 4)
    private String card2;

    @JsonProperty("result")
    @Column(length = 4)
    private String oddeven;

    @JsonProperty("ou")
    @Column(length = 8)
    private String overunder;

    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[4];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[4];

    private boolean closing;

    private boolean cancel;

    //------------------------------------------

    public CrownOddeven(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(this.gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        // 60초 보정
        return this.gameDate.getTime() - System.currentTimeMillis() + 60000 > 0;
    }

    // 스코어 입력
    public void updateScore(CrownOddevenDto.Score score) {
        if (score.isCancel()) {
            this.card1 = "";
            this.card2 = "";
            this.oddeven = "";
            this.overunder = "";
            this.cancel = true;
        } else {
            this.card1 = score.getCard1();
            this.card2 = score.getCard2();
            this.oddeven = score.getOddeven();
            this.overunder = score.getOverunder();

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
                throw new GameZoneException("홀짝 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "oddeven":
                return getOddevenZone("oddeven", "홀", "짝", 0, 1);
            case "overunder":
                return getOddevenZone("overunder", "오버", "언더", 2, 3);
            default:
                throw new GameZoneException("홀짝 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getOddevenZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.CROWN_ODDEVEN);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%04d회차 홀짝", this.round));
        zone.setTeamHome(String.format("%04d회차 %s [%s]", this.round, "oddeven".equals(gameCode) ? "홀짝" : "히든카드", teamHome));
        zone.setTeamAway(String.format("%04d회차 %s [%s]", this.round, "oddeven".equals(gameCode) ? "홀짝" : "히든카드", teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getCrownOddeven().getOdds()[idxHome]);
        zone.setOddsDraw(0);
        zone.setOddsAway(ZoneConfig.getCrownOddeven().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(CrownOddevenDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(card1.equals(score.getCard1()) && card2.equals(score.getCard2()));
    }

}
