package spoon.gameZone.luck;

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
import spoon.support.convert.DoubleArrayConvert;
import spoon.support.convert.LongArrayConvert;

import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
@Entity
@Table(name = "ZONE_LUCK", indexes = {
        @Index(name = "IDX_sdate", columnList = "sdate")
})
public class Luck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    private int round;

    @Column(length = 16)
    private String dealer;

    @Column(length = 16)
    private String player1;

    @Column(length = 16)
    private String player2;

    @Column(length = 16)
    private String player3;

    @Column(length = 16)
    private String result;

    @Column(length = 16)
    private String sdate;

    @Convert(converter = LongArrayConvert.class)
    private long[] amount = new long[15];

    @Convert(converter = DoubleArrayConvert.class)
    private double[] odds = new double[15];

    private boolean closing;

    private boolean cancel;

    //--------------------------------------------------------

    public Luck(int round, Date gameDate) {
        this.round = round;
        this.gameDate = gameDate;
        this.sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");
    }

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - System.currentTimeMillis() > 0;
    }

    // 스코어 입력
    public void updateScore(LuckDto.Score score) {
        if (score.isCancel()) {
            this.dealer = "XX";
            this.player1 = "XX";
            this.player2 = "XX";
            this.player3 = "XX";
            this.result = "XXX";
            this.cancel = true;
        } else {
            this.dealer = score.getDealer1() + score.getDealer2();
            this.player1 = score.getPlayer11() + score.getPlayer12();
            this.player2 = score.getPlayer21() + score.getPlayer22();
            this.player3 = score.getPlayer31() + score.getPlayer32();
            this.result = score.getResult();
            this.cancel = false;
        }
        this.closing = true;
    }

    // 경기 결과
    public ZoneScore getGameResult(String gameCode) {
        switch (gameCode) {
            case "player1":
                return ZoneHelper.zoneResultDraw(result.substring(0, 1));
            case "player2":
                return ZoneHelper.zoneResultDraw(result.substring(1, 2));
            case "player3":
                return ZoneHelper.zoneResultDraw(result.substring(2, 3));
            case "color":
                return ZoneHelper.colorResult(dealer.substring(0, 1));
            case "pattern1":
                return ZoneHelper.pattern1Result(dealer.substring(0, 1), cancel);
            case "pattern2":
                return ZoneHelper.pattern2Result(dealer.substring(0, 1), cancel);
            default:
                throw new GameZoneException("세븐럭 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    public Zone getZone(String gameCode) {
        switch (gameCode) {
            case "player1":
                return getLuckZone("player1", "승", "패", 0, 1, 2);
            case "player2":
                return getLuckZone("player2", "승", "패", 3, 4, 5);
            case "player3":
                return getLuckZone("player3", "승", "패", 6, 7, 8);
            case "color":
                return getLuckZone("color", "레드", "블랙", 9, 10);
            case "pattern1":
                return getLuckZone("pattern1", "스페이드", "하트", 11, 12);
            case "pattern2":
                return getLuckZone("pattern2", "크로바", "다이아", 13, 14);
            default:
                throw new GameZoneException("세븐럭 코드 " + gameCode + " 를 확인 할 수 없습니다.");
        }
    }

    // 플레이어 딜러. 승무패
    private Zone getLuckZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxDraw, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.LUCK);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%03d회차 세븐럭", this.round));
        zone.setTeamHome(String.format("%03d회차 %s [%s]", this.round, gameCode.toUpperCase(), teamHome));
        zone.setTeamAway(String.format("%03d회차 %s [%s]", this.round, gameCode.toUpperCase(), teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getLuck().getOdds()[idxHome]);
        zone.setOddsDraw(ZoneConfig.getLuck().getOdds()[idxDraw]);
        zone.setOddsAway(ZoneConfig.getLuck().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    // 무늬, 칼라. 승패
    private Zone getLuckZone(String gameCode, String teamHome, String teamAway, int idxHome, int idxAway) {
        Zone zone = new Zone();
        zone.setId(this.getId());
        zone.setSdate(this.getSdate());
        zone.setMenuCode(MenuCode.LUCK);
        zone.setGameCode(gameCode);
        zone.setLeague(String.format("%03d회차 세븐럭", this.round));
        zone.setTeamHome(String.format("%03d회차 %s [%s]", this.round, "color".equals(gameCode) ? "칼라" : "무늬", teamHome));
        zone.setTeamAway(String.format("%03d회차 %s [%s]", this.round, "color".equals(gameCode) ? "칼라" : "무늬", teamAway));
        zone.setHandicap(0);
        zone.setOddsHome(ZoneConfig.getLuck().getOdds()[idxHome]);
        zone.setOddsDraw(0);
        zone.setOddsAway(ZoneConfig.getLuck().getOdds()[idxAway]);
        zone.setGameDate(this.gameDate);
        return zone;
    }

    public boolean isChangeResult(LuckDto.Score score) {
        return !this.closing || this.cancel != score.isCancel()
                || !((score.getDealer1() + score.getDealer2()).equals(this.dealer)
                && (score.getPlayer11() + score.getPlayer12()).equals(this.player1)
                && (score.getPlayer21() + score.getPlayer22()).equals(this.player2)
                && (score.getPlayer31() + score.getPlayer32()).equals(this.player3)
                && score.getResult().equals(this.result));
    }

    public String getDealerCard() {
        return makeCardCss(this.dealer);
    }

    public String getPlayer1Card() {
        return makeCardCss(this.player1);
    }

    public String getPlayer2Card() {
        return makeCardCss(this.player2);
    }

    public String getPlayer3Card() {
        return makeCardCss(this.player3);
    }

    public String getCard() {
        if (StringUtils.empty(this.dealer) || this.dealer.length() != 2) return "";
        return this.dealer.substring(0, 1);
    }

    private String makeCardCss(String player) {
        if (StringUtils.empty(player) || player.length() != 2) return "";

        String card = "";

        switch (player.substring(0, 1)) {
            case "S":
                card = "1";
                break;
            case "H":
                card = "2";
                break;
            case "C":
                card = "3";
                break;
            case "D":
                card = "4";
                break;
        }

        switch (player.substring(1, 2)) {
            case "A":
            case "1":
                card += "14";
                break;
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                card += "0" + player.substring(1, 2);
                break;
            case "T":
                card += "10";
                break;
            case "J":
                card += "11";
                break;
            case "Q":
                card += "12";
                break;
            case "K":
                card += "13";
                break;
        }

        return card;
    }
}
