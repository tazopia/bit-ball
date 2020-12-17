package spoon.game.entity;

import lombok.Data;
import org.hibernate.annotations.Formula;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;
import spoon.game.domain.GameCode;
import spoon.game.domain.GameResult;
import spoon.game.domain.MenuCode;
import spoon.game.domain.UpDown;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;

@Data
@Entity
@Table(name = "GAME", indexes = {
        @Index(name = "IDX_gameDate", columnList = "gameDate"),
        @Index(name = "IDX_menuCode", columnList = "menuCode"),
        @Index(name = "IDX_site", columnList = "siteCode, siteId")
})
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32)
    private String siteCode;

    @Column(length = 32)
    private String siteId;

    @Column(length = 32)
    private String groupId;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String sports;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private MenuCode menuCode;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private GameCode gameCode;

    @Column(columnDefinition = "NVARCHAR(128)")
    private String special;

    @Column(columnDefinition = "NVARCHAR(256)")
    private String league;

    @Column(columnDefinition = "NVARCHAR(256)")
    private String teamHome;

    @Column(columnDefinition = "NVARCHAR(256)")
    private String teamAway;

    private double handicap;

    private double oddsHome;

    private double oddsDraw;

    private double oddsAway;

    // 환수율
    private double oddsRate;

    private boolean betHome;

    private boolean betDraw;

    private boolean betAway;

    private long amountHome;

    private long amountDraw;

    private long amountAway;

    @Formula("(amountHome + amountDraw + amountAway)")
    private long amountTotal;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 16)
    private UpDown upDownHome;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 16)
    private UpDown upDownDraw;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 16)
    private UpDown upDownAway;

    private int upCount;

    private Integer scoreHome;

    private Integer scoreAway;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private GameResult result = GameResult.READY;

    private boolean autoUpdate;

    private double sort;

    private boolean enabled;

    private boolean deleted;

    private boolean closing;

    private boolean cancel;

    @Column(length = 32)
    private String ut;

    private Long udt;

    @PrePersist
    @PreUpdate
    public void prePersistUpdate() {
        this.udt = new Date().getTime();
        this.oddsRate = calcOddsRate();
        ++this.upCount;
    }

    private double calcOddsRate() {
        if (this.oddsHome == 0 || this.oddsAway == 0) return 0;
        BigDecimal home = BigDecimal.valueOf(100D).divide(BigDecimal.valueOf(this.oddsHome), MathContext.DECIMAL32);
        BigDecimal draw = oddsDraw > 0 ? BigDecimal.valueOf(100D).divide(BigDecimal.valueOf(this.oddsDraw), MathContext.DECIMAL32) : BigDecimal.valueOf(0);
        BigDecimal away = BigDecimal.valueOf(100D).divide(BigDecimal.valueOf(this.oddsAway), MathContext.DECIMAL32);
        BigDecimal sum = home.add(draw).add(away);

        if (sum.equals(BigDecimal.ZERO)) {
            return 0;
        }

        return BigDecimal.valueOf(100D).divide(sum, MathContext.DECIMAL32).multiply(BigDecimal.valueOf(100D)).doubleValue();
    }

    public String getState() {
        if (this.cancel) {
            return "경기취소";
        } else if (this.closing) {
            return this.result.getName();
        } else if (this.deleted) {
            return "베팅금지";
        } else if (this.gameDate.after(DateUtils.beforeSeconds(Config.getGameConfig().getSportsTime()))) {
            return "경기전";
        } else {
            return "경기중";
        }
    }

    public String getStateCss() {
        if (this.cancel) {
            return "cancel";
        } else if (this.deleted) {
            return "deleted";
        } else if (this.closing) {
            switch (this.result) {
                case HIT:
                    return "hit";
                case CANCEL:
                    return "cancel";
                case DENY:
                    return "delete";
                default:
                    return "";
            }
        } else if (this.gameDate.before(DateUtils.beforeSeconds(Config.getGameConfig().getSportsTime()))) {
            return "ing";
        } else {
            return "";
        }
    }

    public boolean isBeforeGameDate() {
        return this.gameDate.getTime() - Config.getGameConfig().getSportsTime() * 1000 - System.currentTimeMillis() > 0;
    }

    public void updateScore(Integer scoreHome, Integer scoreAway, boolean cancel) {
        if (cancel) {
            this.scoreHome = 0;
            this.scoreAway = 0;
            this.cancel = true;
            this.result = GameResult.CANCEL;
            return;
        }
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;

        BigDecimal home = BigDecimal.valueOf(this.scoreHome);
        BigDecimal away = BigDecimal.valueOf(this.scoreAway);
        BigDecimal handicap = BigDecimal.valueOf(this.handicap);

        if (gameCode == GameCode.MATCH) {
            calcMatch(home, away);
        } else if (gameCode == GameCode.HANDICAP) {
            calcHandicap(home, away, handicap);
        } else {
            calcOverUnder(home, away, handicap);
        }
    }

    private void calcOverUnder(BigDecimal home, BigDecimal away, BigDecimal handicap) {
        double result = home.add(away).subtract(handicap).doubleValue();
        if (result > 0) {
            this.result = GameResult.OVER;
        } else if (result < 0) {
            this.result = GameResult.UNDER;
        } else {
            this.result = GameResult.HIT;
        }
    }

    private void calcHandicap(BigDecimal home, BigDecimal away, BigDecimal handicap) {
        double result = home.subtract(away).add(handicap).doubleValue();
        if (result > 0) {
            this.result = GameResult.HOME;
        } else if (result < 0) {
            this.result = GameResult.AWAY;
        } else {
            this.result = GameResult.HIT;
        }
    }

    private void calcMatch(BigDecimal home, BigDecimal away) {
        double result = home.subtract(away).doubleValue();
        if (result > 0) {
            this.result = GameResult.HOME;
        } else if (result < 0) {
            this.result = GameResult.AWAY;
        } else {
            if (this.oddsDraw > 0) {
                this.result = GameResult.DRAW;
            } else {
                this.result = GameResult.HIT;
            }
        }
    }

    public void updateOdds(double oddsHome, double oddsDraw, double oddsAway) {
        if (this.id == null || this.id == 0) {
            this.oddsHome = oddsHome;
            this.oddsDraw = oddsDraw;
            this.oddsAway = oddsAway;
        }
        updateOddsHome(oddsHome);
        updateOddsDraw(oddsDraw);
        updateOddsAway(oddsAway);
    }

    private void updateOddsHome(double oddsHome) {
        if (this.oddsHome > oddsHome) {
            this.upDownHome = UpDown.DOWN;
        } else if (this.oddsHome < oddsHome) {
            this.upDownHome = UpDown.UP;
        } else {
            this.upDownHome = UpDown.KEEP;
        }

        // 스페셜 쿼터 경기 1.88 고정
        if (this.menuCode == MenuCode.SPECIAL && this.special.contains("쿼터") && (this.gameCode == GameCode.OVER_UNDER || this.gameCode == GameCode.HANDICAP)) {
            oddsHome = 1.88;
        }

        // 승 최고배당 12배
        if (this.gameCode == GameCode.MATCH) {
            switch (sports) {
                case "축구":
                case "아이스하키":
                    if (oddsHome > 12)
                        oddsHome = 12D;
                    break;
                default: // 6
                    if (oddsHome > 6)
                        oddsHome = 6D;
                    break;
            }
        }

        // 축구 스페셜 전반
        if ("축구".equals(this.sports) && this.menuCode == MenuCode.SPECIAL && "전반".equals(special)) {
            if (oddsHome > 6) oddsHome = 6D;
        }

        // 최하배당
        if (oddsHome < 1.02) oddsHome = 1.02D;

        this.oddsHome = oddsHome;
    }

    private void updateOddsDraw(double oddsDraw) {
        if (this.oddsDraw > oddsDraw) {
            this.upDownDraw = UpDown.DOWN;
        } else if (this.oddsDraw < oddsDraw) {
            this.upDownDraw = UpDown.UP;
        } else {
            this.upDownDraw = UpDown.KEEP;
        }

        // 무 최고배당 8배
        switch (sports) {
            case "축구":
            case "아이스하키":
                if (oddsDraw > 8)
                    oddsDraw = 8D;
                break;
        }

        // 축구 스페셜 전반
        if ("축구".equals(this.sports) && this.menuCode == MenuCode.SPECIAL && "전반".equals(special)) {
            if (oddsDraw > 3.5) oddsDraw = 3.5D;
        }

        // 최하배당
//        if (oddsDraw < 1.02) oddsDraw = 1.02D;

        this.oddsDraw = oddsDraw;
    }

    private void updateOddsAway(double oddsAway) {
        if (this.oddsAway > oddsAway) {
            this.upDownAway = UpDown.DOWN;
        } else if (this.oddsAway < oddsAway) {
            this.upDownAway = UpDown.UP;
        } else {
            this.upDownAway = UpDown.KEEP;
        }

        // 스페셜 쿼터 경기 1.88 고정
        if (this.menuCode == MenuCode.SPECIAL && this.special.contains("쿼터") && (this.gameCode == GameCode.OVER_UNDER || this.gameCode == GameCode.HANDICAP)) {
            oddsAway = 1.88;
        }

        // 패 최고배당 10배
        if (this.gameCode == GameCode.MATCH) {
            switch (sports) {
                case "축구":
                case "아이스하키":
                    if (oddsAway > 12)
                        oddsAway = 12D;
                    break;
                default:
                    if (oddsAway > 6)
                        oddsAway = 6D;
                    break;
            }
        }

        // 축구 스페셜 전반
        if ("축구".equals(this.sports) && this.menuCode == MenuCode.SPECIAL && "전반".equals(special)) {
            if (oddsAway > 6) oddsAway = 6D;
        }

        // 최하배당
        if (oddsAway < 1.02) oddsAway = 1.02D;

        this.oddsAway = oddsAway;
    }

    public Sports getSportsBean() {
        Sports sports = Config.getSportsMap().get(this.sports);
        return StringUtils.empty(sports.getSportsName()) ? new Sports(this.sports) : sports;
    }

    public League getLeagueBean() {
        League league = Config.getLeagueMap().get((this.sports + "-" + this.league).toLowerCase());
        return StringUtils.empty(league.getLeagueName()) ? new League(this.sports, this.league) : league;
    }

    public Team getTeamHomeBean() {
        Team team = Config.getTeamMap().get((this.sports + "-" + this.teamHome).toLowerCase());
        return StringUtils.empty(team.getTeamName()) ? new Team(this.sports, this.teamHome) : team;
    }

    public Team getTeamAwayBean() {
        Team team = Config.getTeamMap().get((this.sports + "-" + this.teamAway).toLowerCase());
        return StringUtils.empty(team.getTeamName()) ? new Team(this.sports, this.teamAway) : team;
    }

    public void updateOddRate() {
        this.oddsRate = this.calcOddsRate();
        this.udt = new Date().getTime();
    }
}
