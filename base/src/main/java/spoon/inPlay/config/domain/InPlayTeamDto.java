package spoon.inPlay.config.domain;

import lombok.Data;

public class InPlayTeamDto {

    @Data
    public static class Update {
        private String name;
        private String korName;
    }

    @Data
    public static class Command {
        private String sports;
        private String name;
    }

}
