package spoon.bot.sports.sports.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import spoon.game.domain.GameCode;
import spoon.game.domain.MenuCode;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BotSports {

    private String siteId;

    private String groupId;

    private String siteCode;

    private Date gameDate;

    private String sports;

    private MenuCode menuCode;

    private GameCode gameCode;

    private String special;

    private String league;

    private String leagueFlag;

    private String teamHome;

    private String teamAway;

    private double handicap;

    private double oddsHome;

    private double oddsDraw;

    private double oddsAway;

    private Integer scoreHome;

    private Integer scoreAway;

    private boolean betHome;

    private boolean betDraw;

    private boolean betAway;

    private boolean enabled;

    private boolean closing;

    private boolean cancel;

    private boolean deleted;

    private double sort;

    private String ut;

    private long udt;
}
