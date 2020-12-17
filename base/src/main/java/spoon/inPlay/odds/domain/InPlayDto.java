package spoon.inPlay.odds.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import spoon.common.utils.JsonUtils;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.entity.InPlayLeague;
import spoon.inPlay.config.entity.InPlaySports;
import spoon.inPlay.config.entity.InPlayTeam;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Slf4j
public class InPlayDto {


    @Data
    public static class OddsParam {
        private Long id;
        private String pv;
    }

    @Data
    public static class ScoreParam {
        private Long id;
    }

    @AllArgsConstructor
    @Data
    public static class GameData {
        @JsonProperty("id")
        private Long fixtureId;

        private String sname;

        private String location;

        private String league;

        @Temporal(TemporalType.TIMESTAMP)
        @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date sdate;

        private String hname;

        private String aname;

        @JsonProperty("sts")
        private int status;

        private long ut;

        private Long odds;

        private String score;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        public Date getGameDate() {
            return new Date(this.sdate.getTime() + (1000 * 60 * 60 * 9));
        }

        public String getSportsFlag() {
            return InPlayConfig.getSports().optional(this.sname).map(InPlaySports::getFlag).orElse("sports.png");
        }

        public String getSportsName() {
            return InPlayConfig.getSports().optional(this.sname).map(InPlaySports::getKorName).orElse(this.sname);
        }

        public String getLeagueFlag() {
            return InPlayConfig.getLeague().optional(this.league).map(InPlayLeague::getFlag).orElse("league.gif");
        }

        public String getLeagueName() {
            return InPlayConfig.getLeague().optional(this.league).map(InPlayLeague::getKorName).orElse(this.league);
        }

        public String getTeamHome() {
            return InPlayConfig.getTeam().optional(this.hname).map(InPlayTeam::getKorName).orElse(this.hname);
        }

        public String getTeamAway() {
            return InPlayConfig.getTeam().optional(this.aname).map(InPlayTeam::getKorName).orElse(this.aname);
        }
    }

    @AllArgsConstructor
    @Data
    public static class AdminGameData {
        @JsonProperty("id")
        private Long fixtureId;

        private String sname;

        private String location;

        private String league;

        @Temporal(TemporalType.TIMESTAMP)
        @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date sdate;

        private String hname;

        private String aname;

        @JsonProperty("sts")
        private int status;

        private long ut;

        private Long odds;

        private String score;

        //private LiveScore liveScore;

        public String getSportsFlag() {
            return InPlayConfig.getSports().optional(this.sname).map(InPlaySports::getFlag).orElse("sports.png");
        }

        public String getSportsName() {
            return InPlayConfig.getSports().optional(this.sname).map(InPlaySports::getKorName).orElse(this.sname);
        }

        public String getLeagueFlag() {
            return InPlayConfig.getLeague().optional(this.league).map(InPlayLeague::getFlag).orElse("league.gif");
        }

        public String getLeagueName() {
            return InPlayConfig.getLeague().optional(this.league).map(InPlayLeague::getKorName).orElse(this.league);
        }

        public String getTeamHome() {
            return InPlayConfig.getTeam().optional(this.hname).map(InPlayTeam::getKorName).orElse(this.hname);
        }

        public String getTeamAway() {
            return InPlayConfig.getTeam().optional(this.aname).map(InPlayTeam::getKorName).orElse(this.aname);
        }

        public LiveScore getScoreData() {
            //if (liveScore == null) liveScore =
            return JsonUtils.toModel(this.score, LiveScore.class);
            //return liveScore;
        }

    }
}
