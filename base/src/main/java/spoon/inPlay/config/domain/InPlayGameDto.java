package spoon.inPlay.config.domain;

import lombok.Data;

public class InPlayGameDto {

    @Data
    public static class Config {
        private long money;
        private int win;
        private int max;
        private int min;
    }
}
