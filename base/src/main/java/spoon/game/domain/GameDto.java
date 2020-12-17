package spoon.game.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import spoon.common.utils.DateUtils;
import spoon.config.domain.Config;

import java.util.Date;

@Slf4j
public class GameDto {

    /**
     * 스포츠 검색 dto
     */
    @Data
    public static class Command {
        private String menu;
        private MenuCode menuCode;
        private String sports;
        private String league;
        private String team;
        private String sort;
    }

    /**
     * 관리자 스포츠 베팅 가능 불가능 dto
     */
    @Data
    public static class BetEnabled {
        private long gameId;
        private String betType;
    }

    /**
     * 관리자 스포츠 정보 업데이트 dto
     */
    @Data
    public static class Update {
        private long gameId;
        private String date;
        private String hour;
        private String minute;
        private double oddsHome;
        private double oddsDraw;
        private double oddsAway;
        private double handicap;

        public Date getGameDate() {
            return DateUtils.parse(date + hour + minute, "yyyy.MM.ddHHmm");
        }
    }

    /**
     * 관리자 스코어 입력 dto
     */
    @Data
    public static class Result {
        private long id;
        private Integer scoreHome;
        private Integer scoreAway;
        private boolean cancel;
    }

    /**
     * 게임 승, 패 배당 올리고 내릴때 단위
     */
    @Data
    public static class GameConfig {
        private double oddsDefault = Config.getGameConfig().getOddsDefault();
        private double oddsUp = Config.getGameConfig().getOddsUp();
        private double oddsDown = Config.getGameConfig().getOddsDown();
    }

    /**
     * 메인 페이지 스포츠 리스트
     */
    @Data
    public static class Main {
        private String teamHome;
        private String teamAway;
        private String sports;
        private String league;
        private Date gameDate;
    }

    /**
     * 사용자 스포츠에서 사용되는 리스트
     */
    @Data
    public static class List {
        private long id;

        @JsonProperty(value = "gid")
        private String groupId;

        @JsonProperty(value = "gd")
        private Date gameDate;

        @JsonProperty(value = "sn")
        private String sports;

        @JsonProperty(value = "mc")
        private MenuCode menuCode;

        @JsonProperty(value = "gc")
        private GameCode gameCode;

        @JsonProperty(value = "sp")
        private String special;

        @JsonIgnore
        private String league;

        @JsonIgnore
        private String teamHome;

        @JsonIgnore
        private String teamAway;

        @JsonProperty(value = "ah")
        private double handicap;

        @JsonProperty(value = "oh")
        private double oddsHome;

        @JsonProperty(value = "od")
        private double oddsDraw;

        @JsonProperty(value = "oa")
        private double oddsAway;

        @JsonProperty(value = "bh")
        private boolean betHome;

        @JsonProperty(value = "bd")
        private boolean betDraw;

        @JsonProperty(value = "ba")
        private boolean betAway;

        @JsonIgnore
        private UpDown upDownHome;

        @JsonIgnore
        private UpDown upDownDraw;

        @JsonIgnore
        private UpDown upDownAway;

        @JsonProperty(value = "s")
        private long sort;

        @JsonProperty(value = "r")
        private int rank;

        @JsonProperty(value = "sh")
        private boolean show;

        @JsonProperty(value = "cnt")
        private int count;

        @JsonProperty(value = "btn")
        private boolean btn;

        @JsonProperty(value = "sc")
        private String siteCode;

        @JsonProperty(value = "gdn")
        public String getGameDateName() {
            return DateUtils.format(this.gameDate, "MM/dd(E)");
        }

        @JsonProperty(value = "gtn")
        public String getGameTimeName() {
            return DateUtils.format(this.gameDate, "HH:mm");
        }

        @JsonProperty(value = "sf")
        public String getSportsFlag() {
            return Config.getSportsMap().get(this.sports).getSportsFlag();
        }

        @JsonProperty(value = "ln")
        public String getLeagueName() {
            return Config.getLeagueMap().get((this.sports + "-" + this.league).toLowerCase()).getLeagueKor();
        }

        @JsonProperty(value = "lf")
        public String getLeagueFlag() {
            return Config.getLeagueMap().get((this.sports + "-" + this.league).toLowerCase()).getLeagueFlag();
        }

        @JsonProperty(value = "hn")
        public String getTeamHomeName() {
            return Config.getTeamMap().get((this.sports + "-" + this.teamHome).toLowerCase()).getTeamKor();
        }

        @JsonProperty(value = "an")
        public String getTeamAwayName() {
            return Config.getTeamMap().get((this.sports + "-" + this.teamAway).toLowerCase()).getTeamKor();
        }


        @JsonProperty(value = "bt")
        public String getBeforeTime() {
            long ms = this.gameDate.getTime() - new Date().getTime();
            if (ms < 0) {
                return "진행중";
            }
            int hours = (int) ms / (3600 * 1000);
            if (hours > 0) {
                return hours + "시간전";
            }
            int minute = (int) (ms % (3600 * 1000)) / (60 * 1000);
            return minute + "분전";
        }
    }

    @Data
    public static class Score {
        private long id;
        private long row;
        private String groupId;
        private Date gameDate;
        private String sports;
        private MenuCode menuCode;
        private GameCode gameCode;
        private String special;
        private String league;
        private String teamHome;
        private String teamAway;
        private double handicap;
        private double oddsHome;
        private double oddsDraw;
        private double oddsAway;
        private Integer scoreHome;
        private Integer scoreAway;
        private boolean cancel;
        private boolean deleted;
        private boolean closing;
        private GameResult gameResult;

        public String getGameDateName() {
            return DateUtils.format(this.gameDate, "MM/dd(E)");
        }

        public String getGameTimeName() {
            return DateUtils.format(this.gameDate, "HH:mm");
        }

        public String getSportsFlag() {
            return Config.getSportsMap().get(this.sports).getSportsFlag();
        }

        public String getLeagueName() {
            return Config.getLeagueMap().get((this.sports + "-" + this.league).toLowerCase()).getLeagueKor();
        }

        public String getLeagueFlag() {
            return Config.getLeagueMap().get((this.sports + "-" + this.league).toLowerCase()).getLeagueFlag();
        }

        public String getTeamHomeName() {
            return Config.getTeamMap().get((this.sports + "-" + this.teamHome).toLowerCase()).getTeamKor();
        }

        public String getTeamAwayName() {
            return Config.getTeamMap().get((this.sports + "-" + this.teamAway).toLowerCase()).getTeamKor();
        }

        public String getResultCss() {
            if (this.gameResult == GameResult.CANCEL) {
                return "cancel";
            } else if (this.gameResult == GameResult.HIT) {
                return "hit";
            } else {
                return "";
            }
        }
    }

    @Data
    public static class Sports {
        private String sportsName;

        private String sportsFlag;

        private int cnt;
    }

    @Data
    public static class League {
        @JsonIgnore
        private String sports;

        @JsonIgnore
        private String league;

        private int cnt;

        private int total;

        private boolean show;

        public String getSportsFlag() {
            return Config.getSportsMap().get(this.sports).getSportsFlag();
        }

        public String getSportsName() {
            return Config.getSportsMap().get(this.sports).getSportsName();
        }

        public String getLeagueName() {
            return Config.getLeagueMap().get((this.sports + "-" + this.league).toLowerCase()).getLeagueKor();
        }

        public String getLeagueFlag() {
            return Config.getLeagueMap().get((this.sports + "-" + this.league).toLowerCase()).getLeagueFlag();
        }
    }

    @Data
    public static class Bot {
        private long id;
        private String ut;
    }
}
