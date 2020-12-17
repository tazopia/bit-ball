package spoon.game.domain;

import lombok.Data;

public class LeagueDto {

    @Data
    public static class Command {
        private String sports;
        private String league;
    }

    @Data
    public static class Add {
        private String sports;
        private String league;
        private boolean enabled;
        public String getKey() {
            return sports + "-" + league;
        }
    }

    @Data
    public static class Update {
        private long id;
        private String sports;
        private boolean enabled;
        private String leagueKor;
    }

    @Data
    public static class List {
        private String sports;
        private String leagueName;
        private String leagueKor;
    }
}
