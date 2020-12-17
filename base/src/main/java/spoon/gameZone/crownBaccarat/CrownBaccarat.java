package spoon.gameZone.crownBaccarat;

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
@Table(name = "ZONE_CW_BACCARAT", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class CrownBaccarat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    @Column(length = 8)
    private String p;

    @Column(length = 8)
    private String c;

    @Column(length = 8)
    private String b;

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

    public CrownBaccarat(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(this.gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        // 60초 보정
        return this.gameDate.getTime() - System.currentTimeMillis() + 60000 > 0;
    }

    // 스코어 입력
    public void updateScore(CrownBaccaratDto.Score score) {
        if (score.isCancel()) {
            this.p = "";
            this.b = "";
            this.c = "";
            this.result = "";
            this.cancel = true;
        } else {
            this.p = score.getP();
            this.b = score.getB();
            this.c = score.getC();
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
                return getBaccaratZone("baccarat", "PLAYER", "BANKER", 0, 1, 2);
            default:
                throw new GameZoneException("홀짝 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getBaccaratZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxDraw, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.CROWN_BACCARAT);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%04d회차 바카라", this.round));
        zone.setTeamHome(String.format("%04d회차 [%s]", this.round, teamHome));
        zone.setTeamAway(String.format("%04d회차 [%s]", this.round, teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getCrownBaccarat().getOdds()[idxHome]);
        zone.setOddsDraw(ZoneConfig.getCrownBaccarat().getOdds()[idxDraw]);
        zone.setOddsAway(ZoneConfig.getCrownBaccarat().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(CrownBaccaratDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(p.equals(score.getP()) && b.equals(score.getB()) && c.equals(score.getC()));
    }

    public String getWin() {
        if ("P".equals(this.result)) {
            return "PLAYER";
        } else if ("T".equals(this.result)) {
            return "TIE";
        } else if ("B".equals(this.result)) {
            return "BANKER";
        } else {
            return "";
        }
    }
}
