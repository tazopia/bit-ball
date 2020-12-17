package spoon.event.domain;

import lombok.Data;

public class DailyDto {

    @Data
    public static class Command {
        private String sdate;
        private String userid;
    }
}
