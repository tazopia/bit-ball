package spoon.inPlay.config.domain;

import lombok.Data;

public class InPlayMarketDto {

    @Data
    public static class Command {
        private String name;
    }

    @Data
    public static class Update {
        private Long id;
        private String korName;
        private int line;
        private String menu;
        private boolean enabled;
        private boolean team;
        private InPlayMinMax minMax;
    }
}
