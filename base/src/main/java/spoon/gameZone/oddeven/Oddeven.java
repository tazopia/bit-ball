package spoon.gameZone.oddeven;

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
@Table(name = "ZONE_ODDEVEN", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Oddeven {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    // 히든카드
    @Column(length = 4)
    private String card1;

    // 오픈카드
    @Column(length = 4)
    private String card2;

    @JsonProperty("result")
    @Column(length = 4)
    private String oddeven;

    @Column(length = 8)
    private String overunder;

    @Column(length = 16)
    private String pattern;

    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[8];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[8];

    private boolean closing;

    private boolean cancel;

    //------------------------------------------

    public Oddeven(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(this.gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        // 60초 보정
        return this.gameDate.getTime() - System.currentTimeMillis() + 60000 > 0;
    }

    // 스코어 입력
    public void updateScore(OddevenDto.Score score) {
        if (score.isCancel()) {
            this.card1 = "";
            this.card2 = "";
            this.oddeven = "";
            this.overunder = "";
            this.pattern = "";
            this.cancel = true;
        } else {
            this.card1 = score.getCard1();
            this.card2 = score.getCard2();
            this.oddeven = score.getOddeven();
            this.overunder = score.getOverunder();
            this.pattern = score.getPattern();

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
            case "pattern1":
                return ZoneHelper.pattern1Result(pattern, cancel);
            case "pattern2":
                return ZoneHelper.pattern2Result(pattern, cancel);
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
            case "pattern1":
                return getOddevenZone("pattern1", "스페이드", "하트", 4, 5);
            case "pattern2":
                return getOddevenZone("pattern2", "크로바", "다이아", 6, 7);
            default:
                throw new GameZoneException("홀짝 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getOddevenZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.ODDEVEN);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%04d회차 홀짝", this.round));
        zone.setTeamHome(String.format("%04d회차 %s [%s]", this.round, "oddeven".equals(gameCode) ? "홀짝" : "히든카드", teamHome));
        zone.setTeamAway(String.format("%04d회차 %s [%s]", this.round, "oddeven".equals(gameCode) ? "홀짝" : "히든카드", teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getOddeven().getOdds()[idxHome]);
        zone.setOddsDraw(0);
        zone.setOddsAway(ZoneConfig.getOddeven().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(OddevenDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(card1.equals(score.getCard1()) && card2.equals(score.getCard2()));
    }

}
