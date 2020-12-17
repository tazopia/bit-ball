package spoon.gameZone.baccarat;

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
@Table(name = "ZONE_BACCARAT", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Baccarat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    @Column(length = 8)
    private String p1;

    @Column(length = 8)
    private String p2;

    @Column(length = 8)
    private String p3 = "";

    @Column(length = 8)
    private String b1;

    @Column(length = 8)
    private String b2;

    @Column(length = 8)
    private String b3 = "";

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

    public Baccarat(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(this.gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        // 60초 보정
        return this.gameDate.getTime() - System.currentTimeMillis() + 60000 > 0;
    }

    // 스코어 입력
    public void updateScore(BaccaratDto.Score score) {
        if (score.isCancel()) {
            this.p1 = "";
            this.p2 = "";
            this.p3 = "";
            this.b1 = "";
            this.b2 = "";
            this.b3 = "";
            this.result = "";
            this.cancel = true;
        } else {
            this.p1 = score.getP1();
            this.p2 = score.getP2();
            this.p3 = score.getP3();
            this.b1 = score.getB1();
            this.b2 = score.getB2();
            this.b3 = score.getB3();
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
        zone.setMenuCode(MenuCode.BACCARAT);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%04d회차 바카라", this.round));
        zone.setTeamHome(String.format("%04d회차 [%s]", this.round, teamHome));
        zone.setTeamAway(String.format("%04d회차 [%s]", this.round, teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getBaccarat().getOdds()[idxHome]);
        zone.setOddsDraw(ZoneConfig.getBaccarat().getOdds()[idxDraw]);
        zone.setOddsAway(ZoneConfig.getBaccarat().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(BaccaratDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(p1.equals(score.getP1()) && p2.equals(score.getP2()) && p3.equals(score.getP3())
                && b1.equals(score.getB1()) && b2.equals(score.getB2()) && b3.equals(score.getB3()));
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
