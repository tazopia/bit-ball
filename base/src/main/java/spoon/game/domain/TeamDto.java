package spoon.game.domain;

import lombok.Data;

public class TeamDto {

    @Data
    public static class Command {
        private String sports;
        private String team;
    }

    @Data
    public static class Update {
        private long id;
        private String team;
    }

    @Data
    public static class PopupUpdate {
        private long id;
        private String teamKor;
    }

    @Data
    public static class Add {
        private String sports;
        private String teamName;
        private String teamKor;

        public String getKey() {
            return this.sports + "-" + this.teamName;
        }
    }
}
