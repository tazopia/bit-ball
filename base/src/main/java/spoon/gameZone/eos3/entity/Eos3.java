package spoon.gameZone.eos3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.bot.support.ZoneHelper;
import spoon.common.utils.DateUtils;
import spoon.game.domain.GameResult;
import spoon.game.domain.MenuCode;
import spoon.gameZone.GameZoneException;
import spoon.gameZone.Zone;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneScore;
import spoon.gameZone.eos3.domain.Eos3Dto;
import spoon.support.convert.DoubleArrayConvert;
import spoon.support.convert.LongArrayConvert;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
@Entity
@Table(name = "ZONE_EOS3", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Eos3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    // 일반볼 콤마로 연결
    @Column(length = 64)
    private String ball;

    // 파워볼
    @Column(length = 16)
    private String pb;

    @Column(length = 16)
    private String oddeven;

    @Column(length = 16)
    private String pb_oddeven;

    @Column(length = 16)
    private String overunder;

    @Column(length = 16)
    private String pb_overunder;

    @Column(length = 16)
    private String size;

    private int sum;

    @Column(length = 16)
    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[25];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[25];

    private boolean closing;

    private boolean cancel;

    //----------------------------------------------------------------

    public Eos3(int round, Date gameDate) {
        this.round = round > 480 ? round % 480 : round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    // 스코어 입력
    public void updateScore(Eos3Dto.Score score) {
        if (score.isCancel()) {
            this.pb = "";
            this.ball = "";
            this.oddeven = "";
            this.pb_oddeven = "";
            this.overunder = "";
            this.pb_overunder = "";
            this.size = "";
            this.cancel = true;
        } else {
            int[] balls = Arrays.stream(score.getBall().trim().split(",")).mapToInt(x -> Integer.parseInt(x, 10)).toArray();
            int pball = Integer.parseInt(score.getPb(), 10);
            int sum = IntStream.of(balls).sum();

            this.oddeven = sum % 2 == 1 ? "ODD" : "EVEN";
            this.pb_oddeven = pball % 2 == 1 ? "ODD" : "EVEN";
            this.overunder = sum < 73 ? "UNDER" : "OVER";
            this.pb_overunder = pball < 5 ? "UNDER" : "OVER";
            this.size = sum > 80 ? "B" : (sum < 65 ? "S" : "M");
            this.pb = score.getPb();
            this.ball = Arrays.stream(balls).mapToObj(String::valueOf).collect(Collectors.joining(","));
            this.sum = sum;
            this.cancel = false;
        }
        this.closing = true;
    }

    // 경기 결과
    // ODD/EVEN, OVER/UNDER, 대/중/소
    public ZoneScore getGameResult(String gameCode) {
        switch (gameCode) {
            case "oddeven":
                return ZoneHelper.zoneResult(oddeven);
            case "pb_oddeven":
                return ZoneHelper.zoneResult(pb_oddeven);
            case "overunder":
                return ZoneHelper.zoneResult(overunder);
            case "pb_overunder":
                return ZoneHelper.zoneResult(pb_overunder);
            case "size":
                return ZoneHelper.zoneResult(size);
            case "odd_ou":
                return "EVEN".equals(oddeven) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(overunder);
            case "even_ou":
                return "ODD".equals(oddeven) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(overunder);
            case "odd_size":
                return "EVEN".equals(oddeven) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(size);
            case "even_size":
                return "ODD".equals(oddeven) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(size);
            case "pb_odd_ou":
                return "EVEN".equals(pb_oddeven) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(pb_overunder);
            case "pb_even_ou":
                return "ODD".equals(pb_oddeven) ? new ZoneScore(0, 0, GameResult.NONE)
                        : ZoneHelper.zoneResult(pb_overunder);
            default:
                throw new GameZoneException("EOS3 파워볼 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private ZoneScore oeOuResult(String oddeven, String overunder) {

        return null;
    }

    public Zone getZone(String gameCode) {
        Zone zone;
        switch (gameCode) {
            case "oddeven":
                return getPowerZone("oddeven", "일반볼", "홀", "짝", 0, 1);
            case "pb_oddeven":
                return getPowerZone("pb_oddeven", "파워볼", "홀", "짝", 2, 3);
            case "overunder":
                return getPowerZone("overunder", "일반볼", "오버", "언더", 4, 5);
            case "pb_overunder":
                return getPowerZone("pb_overunder", "파워볼", "오버", "언더", 6, 7);
            case "size":
                zone = getPowerZone("size", "", "대", "소", 8, 10);
                zone.setOddsDraw(ZoneConfig.getPower().getOdds()[9]);
                return zone;
            case "odd_ou":
                return getPowerZone("odd_ou", "일반볼", "홀 [오버]", "홀 [언더]", 11, 12);
            case "even_ou":
                return getPowerZone("even_ou", "일반볼", "짝 [오버]", "짝 [언더]", 13, 14);
            case "odd_size":
                zone = getPowerZone("odd_size", "일반볼", "홀 [대]", "홀 [소]", 15, 17);
                zone.setOddsDraw(ZoneConfig.getPower().getOdds()[16]);
                return zone;
            case "even_size":
                zone = getPowerZone("even_size", "일반볼", "짝 [대]", "짝 [소]", 18, 20);
                zone.setOddsDraw(ZoneConfig.getPower().getOdds()[19]);
                return zone;
            case "pb_odd_ou":
                return getPowerZone("pb_odd_ou", "파워볼", "홀 [오버]", "홀 [언더]", 21, 22);
            case "pb_even_ou":
                return getPowerZone("pb_even_ou", "파워볼", "짝 [오버]", "짝 [언더]", 23, 24);
            default:
                throw new GameZoneException("파워볼 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    private Zone getPowerZone(String gameCode, String type, String teamHome, String teamAway, int idxHome, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.EOS3);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%03d회차 EOS 파워볼 3분", this.round));
        zone.setTeamHome(String.format("%03d회차 %s [%s]", this.round, type, teamHome));
        zone.setTeamAway(String.format("%03d회차 %s [%s]", this.round, type, teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getEos3().getOdds()[idxHome]);
        zone.setOddsDraw(0);
        zone.setOddsAway(ZoneConfig.getEos3().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(Eos3Dto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !(score.getPb().equals(this.pb) && score.getBall().equals(this.ball));

    }
}
