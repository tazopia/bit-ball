package spoon.gameZone.crownSutda;

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
@Table(name = "ZONE_CW_SUTDA", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class CrownSutda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    @Column(length = 8)
    private String korea;

    @Column(length = 8)
    private String japan;

    @Column(length = 8)
    private String k1;

    @Column(length = 8)
    private String k2;

    @Column(length = 8)
    private String j1;

    @Column(length = 8)
    private String j2;

    @Column(length = 16)
    private String result;

    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[3];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[3];

    private boolean closing;

    private boolean cancel;

    //-----------------------------------------------------

    public CrownSutda(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(this.gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        // 60초 보정
        return this.gameDate.getTime() - System.currentTimeMillis() + 60000 > 0;
    }

    // 스코어 입력
    public void updateScore(CrownSutdaDto.Score score) {
        if (score.isCancel()) {
            this.k1 = "";
            this.k2 = "";
            this.j1 = "";
            this.j2 = "";
            this.korea = "";
            this.japan = "";
            this.result = "";
            this.cancel = true;
        } else {
            this.k1 = score.getK1();
            this.k2 = score.getK2();
            this.j1 = score.getJ1();
            this.j2 = score.getJ2();
            this.korea = score.getKorea();
            this.japan = score.getJapan();
            this.result = score.getResult();
            this.cancel = false;
        }
        this.closing = true;
    }

    // 경기 결과
    public ZoneScore getGameResult(String gameCode) {
        switch (gameCode) {
            case "baccarat":
                return ZoneHelper.zoneResultDraw(result);
            default:
                throw new GameZoneException("바카라 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "baccarat":
                return getBaccaratZone("baccarat", "KOREA", "JAPAN", 0, 1, 2);
            default:
                throw new GameZoneException("홀짝 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getBaccaratZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxDraw, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.CROWN_SUTDA);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%04d회차 섰다", this.round));
        zone.setTeamHome(String.format("%04d회차 [%s]", this.round, teamHome));
        zone.setTeamAway(String.format("%04d회차 [%s]", this.round, teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getCrownSutda().getOdds()[idxHome]);
        zone.setOddsDraw(ZoneConfig.getCrownSutda().getOdds()[idxDraw]);
        zone.setOddsAway(ZoneConfig.getCrownSutda().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(CrownSutdaDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(korea.equals(score.getKorea()) && japan.equals(score.getJapan()));
    }

    public String getWin() {
        if ("K".equals(this.result)) {
            return "KOREA";
        } else if ("T".equals(this.result)) {
            return "TIE";
        } else if ("J".equals(this.result)) {
            return "JAPAN";
        } else {
            return "";
        }
    }
}
