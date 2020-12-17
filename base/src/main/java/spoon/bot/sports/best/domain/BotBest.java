package spoon.bot.sports.best.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BotBest {

    private long id;

    private String sports;

    private String league;

    private String flag;

    private String gameDate;

    private String teamHome;

    private String teamAway;

    private double handicap;

    private double oddsHome;

    private double oddsDraw;

    private double oddsAway;

    private Integer scoreHome;

    private Integer scoreAway;

    private String status;

    private String gameCode;

    private String result;

    private String special;

    private boolean cancel;

    private long ut;

    public String getSiteCode() {
        return "best";
    }

    public String leagueKey() {
        return this.sports + "-" + this.league;
    }

    public String teamHomeKey() {
        return this.sports + "-" + this.league + "-" + this.teamHome;
    }

    public String teamAwayKey() {
        return this.sports + "-" + this.league + "-" + this.teamAway;
    }
}
