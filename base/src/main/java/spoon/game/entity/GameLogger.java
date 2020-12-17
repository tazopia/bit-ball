package spoon.game.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.game.domain.GameCode;
import spoon.game.domain.MenuCode;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "GAME_LOGGER", indexes = {
        @Index(name = "IDX_gameId", columnList = "gameId")
})
public class GameLogger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long gameId;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private MenuCode menuCode;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private GameCode gameCode;

    @Column(columnDefinition = "nvarchar(64)")
    private String sports;

    @Column(columnDefinition = "nvarchar(128)")
    private String special;

    @Column(columnDefinition = "nvarchar(256)")
    private String league;

    private String leagueFlag;

    @Column(columnDefinition = "nvarchar(256)")
    private String teamHome;

    @Column(columnDefinition = "nvarchar(256)")
    private String teamAway;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date gameDate;

    private double handicap;

    private double oddsHome;

    private double oddsDraw;

    private double oddsAway;

    private boolean betHome;

    private boolean betDraw;

    private boolean betAway;

    private boolean autoUpdate;

    private boolean enabled;

    private boolean deleted;

    private boolean closing;

    private Integer scoreHome;

    private Integer scoreAway;

    private double oddsRate;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date changeDate;

    @Column(length = 32)
    private String userid;

    @Column(length = 32)
    private String ip;

    public GameLogger(Game game) {
        this(game, WebUtils.userid(), WebUtils.ip());
    }

    public GameLogger(Game game, String userid, String ip) {
        this.gameId = game.getId();
        this.menuCode = game.getMenuCode();
        this.gameCode = game.getGameCode();
        this.sports = game.getSports();
        this.special = game.getSpecial();
        this.league = game.getLeagueBean().getLeagueKor();
        this.leagueFlag = game.getLeagueBean().getLeagueFlag();
        this.teamHome = game.getTeamHomeBean().getTeamKor();
        this.teamAway = game.getTeamAwayBean().getTeamKor();
        this.gameDate = game.getGameDate();
        this.handicap = game.getHandicap();
        this.oddsHome = game.getOddsHome();
        this.oddsDraw = game.getOddsDraw();
        this.oddsAway = game.getOddsAway();
        this.betHome = game.isBetHome();
        this.betDraw = game.isBetDraw();
        this.betAway = game.isBetAway();
        this.autoUpdate = game.isAutoUpdate();
        this.enabled = game.isEnabled();
        this.deleted = game.isDeleted();
        this.closing = game.isClosing();
        this.scoreHome = game.getScoreHome();
        this.scoreAway = game.getScoreAway();
        this.oddsRate = game.getOddsRate();
        this.changeDate = new Date();
        this.userid = userid;
        this.ip = ip;
    }

    public String getSportsFlag() {
        return Config.getSportsMap().get(this.sports).getSportsFlag();
    }

}
