package spoon.event.domain;

import lombok.Data;

public class LottoDto {

    @Data
    public static class Command {
        private String sdate;
        private String userid;
    }
}
