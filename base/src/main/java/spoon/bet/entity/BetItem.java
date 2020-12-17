package spoon.bet.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.game.domain.GameCode;
import spoon.game.domain.GameResult;
import spoon.game.domain.MenuCode;
import spoon.game.entity.Game;
import spoon.gameZone.Zone;
import spoon.gameZone.ZoneScore;
import spoon.member.domain.Role;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "BET_ITEM", indexes = {
        @Index(name = "IDX_userid", columnList = "userid"),
        @Index(name = "IDX_gameId", columnList = "gameId"),
        @Index(name = "IDX_groupId", columnList = "groupId")
})
public class BetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "betId", insertable = false, updatable = false)
    private Bet bet;

    private Long gameId;

    // 동일경기 여부
    private String groupId;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    @Column(columnDefinition = "nvarchar(64)")
    private String sports;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private MenuCode menuCode;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private GameCode gameCode;

    @Column(columnDefinition = "nvarchar(128)")
    private String special;

    @Column(columnDefinition = "nvarchar(256)")
    private String league;

    @Column(columnDefinition = "nvarchar(256)")
    private String teamHome;

    @Column(columnDefinition = "nvarchar(256)")
    private String teamAway;

    private double handicap;

    private double oddsHome;

    private double oddsDraw;

    private double oddsAway;

    private double odds;

    private String betTeam;

    private int betZone = -1;

    private Integer scoreHome;

    private Integer scoreAway;

    private long betMoney;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private GameResult gameResult = GameResult.READY;

    private String result = "대기";

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    private String userid;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Role role;

    private boolean cancel;

    private boolean closing;

    @Transient
    private String siteCode;

    public String getResultString() {
        if ("대기".equals(this.result)) {
            if (gameDate.after(new Date())) {
                return "경기전";
            } else {
                return "진행중";
            }
        } else {
            return this.result;
        }
    }

    public String getResultStringCss() {
        if ("대기".equals(this.result)) {
            if (gameDate.after(new Date())) {
                return "";
            } else {
                return "ing";
            }
        } else {
            switch (this.result) {
                case "취소":
                case "베팅취소":
                case "경기취소":
                case "진행중취소":
                    return "cancel";
                case "적중":
                    return "win";
                case "미적중":
                    return "lose";
                case "적특":
                    return "hit";
                default:
                    return "";
            }
        }
    }

    public BetItem(Game game, String userid, Role role, String pos, long betMoney) {
        this.gameId = game.getId();
        this.groupId = game.getGroupId();
        this.sports = game.getSports();
        this.league = game.getLeagueBean().getLeagueKor();
        this.special = game.getSpecial();
        this.menuCode = game.getMenuCode();
        this.gameCode = game.getGameCode();
        this.teamHome = game.getTeamHomeBean().getTeamKor();
        this.teamAway = game.getTeamAwayBean().getTeamKor();
        this.handicap = game.getHandicap();
        this.oddsHome = game.getOddsHome();
        this.oddsDraw = game.getOddsDraw();
        this.oddsAway = game.getOddsAway();
        this.gameDate = game.getGameDate();
        this.betTeam = pos;
        this.betMoney = betMoney;
        this.userid = userid;
        this.role = role;
        this.siteCode = game.getSiteCode();

        switch (pos) {
            case "home":
                this.odds = game.getOddsHome();
                break;
            case "draw":
                this.odds = game.getOddsDraw();
                break;
            case "away":
                this.odds = game.getOddsAway();
                break;
        }
    }

    public BetItem(Zone zone, String userid, Role role, String betTeam, int betZone, long betMoney, double odds) {
        this.gameId = zone.getId();
        this.groupId = zone.getSdate();
        this.sports = zone.getMenuCode().getName();
        this.league = zone.getLeague();
        this.special = zone.getGameCode();
        this.menuCode = zone.getMenuCode();
        this.gameCode = GameCode.ZONE;
        this.teamHome = zone.getTeamHome();
        this.teamAway = zone.getTeamAway();
        this.handicap = zone.getHandicap();
        this.oddsHome = zone.getOddsHome();
        this.oddsDraw = zone.getOddsDraw();
        this.oddsAway = zone.getOddsAway();
        this.gameDate = zone.getGameDate();
        this.betTeam = betTeam;
        this.betZone = betZone;
        this.betMoney = betMoney;
        this.userid = userid;
        this.role = role;
        this.odds = odds;

    }

    public boolean isStart() {
        return this.gameDate.before(new Date());
    }

    public boolean updateScore(int scoreHome, int scoreAway, boolean cancel) {
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;
        this.cancel = cancel;
        this.closing = true;

        if (this.cancel) {
            this.gameResult = GameResult.CANCEL;
            this.result = "경기취소";
        } else {
            if (this.gameCode == GameCode.MATCH) {
                calcMatch();
            } else if (this.gameCode == GameCode.HANDICAP) {
                calcHandicap();
            } else if (this.gameCode == GameCode.OVER_UNDER) {
                calcOverUnder();
            }
        }
        return this.bet.betResultOdds();
    }

    public boolean updateScore(ZoneScore zoneScore) {
        this.gameResult = zoneScore.getGameResult();
        this.scoreHome = zoneScore.getScoreHome();
        this.scoreAway = zoneScore.getScoreAway();
        this.closing = true;

        switch (gameResult) {
            case CANCEL:
                this.cancel = true;
                this.result = "취소";
                break;
            case HOME:
            case OVER:
                this.result = this.betTeam.equals("home") ? "적중" : "미적중";
                break;
            case DRAW:
                this.result = this.betTeam.equals("draw") ? "적중" : "미적중";
                break;
            case AWAY:
            case UNDER:
                this.result = this.betTeam.equals("away") ? "적중" : "미적중";
                break;
            case DRAW_HIT:
                this.result = this.betTeam.equals("draw") ? "적중" : "적특";
                break;
            case HIT:
                this.result = "적특";
                break;
            case NONE:
                this.result = "미적중";
                break;
            default:
                throw new RuntimeException("결과처리를 할 수 없습니다. : " + gameResult);
        }
        return this.bet.betZoneOdds();
    }

    private void calcOverUnder() {
        double result = this.scoreHome + this.scoreAway - this.handicap;
        if (result > 0) {
            this.gameResult = GameResult.OVER;
            this.result = this.betTeam.equals("home") ? "적중" : "미적중";
        } else if (result < 0) {
            this.gameResult = GameResult.UNDER;
            this.result = this.betTeam.equals("away") ? "적중" : "미적중";
        } else {
            this.gameResult = GameResult.HIT;
            this.result = "적특";
        }
    }

    private void calcHandicap() {
        double result = this.scoreHome - this.scoreAway + this.handicap;
        if (result > 0) {
            this.gameResult = GameResult.HOME;
            this.result = this.betTeam.equals("home") ? "적중" : "미적중";
        } else if (result < 0) {
            this.gameResult = GameResult.AWAY;
            this.result = this.betTeam.equals("away") ? "적중" : "미적중";
        } else {
            this.gameResult = GameResult.HIT;
            this.result = "적특";
        }
    }

    private void calcMatch() {
        double result = this.scoreHome - this.scoreAway;
        if (result > 0) {
            this.gameResult = GameResult.HOME;
            this.result = this.betTeam.equals("home") ? "적중" : "미적중";
        } else if (result < 0) {
            this.gameResult = GameResult.AWAY;
            this.result = this.betTeam.equals("away") ? "적중" : "미적중";
        } else {
            if (this.oddsDraw > 0) {
                this.gameResult = GameResult.DRAW;
                this.result = this.betTeam.equals("draw") ? "적중" : "미적중";
            } else {
                this.gameResult = GameResult.HIT;
                this.result = "적특";
            }
        }
    }

    public void reset() {
        if ("경기취소".equals(this.result)) this.cancel = false;
        this.result = "대기";
        this.scoreHome = null;
        this.scoreAway = null;
        this.gameResult = GameResult.READY;
        this.closing = false;
        this.bet.betResultOdds();
    }


    public boolean isCanCancel() {
        if (!this.cancel) {
            return !(this.gameCode == GameCode.ZONE && this.isStart());
        } else {
            return false;
        }
    }

    public void updateGameDate(Date gameDate) {
        this.gameDate = gameDate;
        this.getBet().updateGameDate();
    }
}
