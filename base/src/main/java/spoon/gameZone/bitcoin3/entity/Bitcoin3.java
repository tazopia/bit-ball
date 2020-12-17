package spoon.gameZone.bitcoin3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.bot.support.ZoneHelper;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;
import spoon.game.domain.GameResult;
import spoon.game.domain.MenuCode;
import spoon.gameZone.GameZoneException;
import spoon.gameZone.Zone;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneScore;
import spoon.gameZone.bitcoin3.domain.Bitcoin3Dto;
import spoon.gameZone.bitcoin3.domain.Bitcoin3Json;
import spoon.support.convert.DoubleArrayConvert;
import spoon.support.convert.LongArrayConvert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
@Entity
@Table(name = "ZONE_BITCOIN3", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Bitcoin3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    @Column(name = "s_price")
    private double open;

    @Column(name = "e_price")
    private double close;

    private double high;

    private double low;

    @Column(length = 8)
    private String bs;

    private int hi;

    private int lo;

    // 하이홀짝
    @Column(length = 16)
    private String hi_oe;

    // 하이언오버
    @Column(length = 16)
    private String hi_ou;

    // 로우홀짝
    @Column(length = 16)
    private String lo_oe;

    // 로우언오버
    @Column(length = 16)
    private String lo_ou;

    @Column(length = 16)
    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[16];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[16];

    private boolean closing;

    private boolean cancel;

    private Bitcoin3(LocalDateTime gameDate) {
        this.round = Integer.parseInt(gameDate.format(DateTimeFormatter.ofPattern("HHmm")));
        this.sdate = gameDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        this.gameDate = DateUtils.parse(this.sdate, "yyyyMMddHHmm");
    }

    public static Bitcoin3 of(LocalDateTime gameDate) {
        return new Bitcoin3(gameDate);
    }

    //--------------------------------------------------------

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    public ZoneScore getGameResult(String gameCode) {
        switch (gameCode) {
            case "hi_oe":
                return ZoneHelper.zoneResult(hi_oe);
            case "hi_ou":
                return ZoneHelper.zoneResult(hi_ou);
            case "lo_oe":
                return ZoneHelper.zoneResult(lo_oe);
            case "lo_ou":
                return ZoneHelper.zoneResult(lo_ou);
            case "hi_odd_ou":
                return "EVEN".equals(hi_oe) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(hi_ou);
            case "hi_even_ou":
                return "ODD".equals(hi_oe) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(hi_ou);
            case "lo_odd_ou":
                return "EVEN".equals(lo_oe) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(lo_ou);
            case "lo_even_ou":
                return "ODD".equals(lo_oe) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(lo_ou);
            default:
                throw new GameZoneException("비트코인 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public boolean isChangeResult(Bitcoin3Dto.Score score) {
        return score.getHigh() != this.high || score.getLow() != this.low || !score.getBs().equals(this.bs);
    }

    public void updateScore(Bitcoin3Dto.Score score) {
        if (score.isCancel()) {
            this.closing = true;
            this.cancel = true;
            return;
        }

        this.low = score.getLow();
        this.high = score.getHigh();
        this.open = score.getOpen();
        this.close = score.getClose();
        this.bs = score.getBs();

        this.hi = StringUtils.lastNumber(this.high);
        this.lo = StringUtils.lastNumber(this.low);

        this.hi_oe = this.hi % 2 == 0 ? "EVEN" : "ODD";
        this.hi_ou = this.hi < 5 ? "UNDER" : "OVER";
        this.lo_oe = this.lo % 2 == 0 ? "EVEN" : "ODD";
        this.lo_ou = this.lo < 5 ? "UNDER" : "OVER";

        this.closing = true;
        this.cancel = false;
    }

    public void updateScore(Bitcoin3Json score) {
        this.low = score.getLow();
        this.high = score.getHigh();
        this.open = score.getOpen();
        this.close = score.getClose();
        this.bs = score.getBs();

        this.hi = StringUtils.lastNumber(this.high);
        this.lo = StringUtils.lastNumber(this.low);
        // ---------------------------------------
        this.hi_oe = this.hi % 2 == 0 ? "EVEN" : "ODD";
        this.hi_ou = this.hi < 5 ? "UNDER" : "OVER";
        this.lo_oe = this.lo % 2 == 0 ? "EVEN" : "ODD";
        this.lo_ou = this.lo < 5 ? "UNDER" : "OVER";

        this.closing = true;
        this.cancel = false;
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "hi_oe":
                return getBitcoin3Zone("hi_oe", "고가", "홀", "짝", 0, 1);
            case "hi_ou":
                return getBitcoin3Zone("hi_ou", "고가", "오버", "언더", 2, 3);
            case "lo_oe":
                return getBitcoin3Zone("lo_oe", "저가", "홀", "짝", 4, 5);
            case "lo_ou":
                return getBitcoin3Zone("lo_ou", "저가", "오버", "언더", 6, 7);
            case "hi_odd_ou":
                return getBitcoin3Zone("hi_odd_ou", "고가", "홀/오버", "홀/언더", 8, 9);
            case "hi_even_ou":
                return getBitcoin3Zone("hi_even_ou", "고가", "짝/오버", "짝/언더", 10, 11);
            case "lo_odd_ou":
                return getBitcoin3Zone("lo_odd_ou", "저가", "홀/오버", "홀/언더", 12, 13);
            case "lo_even_ou":
                return getBitcoin3Zone("lo_even_ou", "저가", "짝/오버", "짝/언더", 14, 15);
            default:
                throw new GameZoneException("비트코인 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getBitcoin3Zone(String gameCode, String type, String teamHome, String teamAway, int idxHome, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.BITCOIN3);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%03d회차 BTC 3분", this.round));
        zone.setTeamHome(String.format("%03d회차 %s [%s]", this.round, type, teamHome));
        zone.setTeamAway(String.format("%03d회차 %s [%s]", this.round, type, teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getBitcoin3().getOdds()[idxHome]);
        zone.setOddsDraw(0);
        zone.setOddsAway(ZoneConfig.getBitcoin3().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }
}
