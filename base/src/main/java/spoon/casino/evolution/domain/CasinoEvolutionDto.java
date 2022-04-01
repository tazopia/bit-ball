package spoon.casino.evolution.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class CasinoEvolutionDto {

    @Data
    public static class SendMoney {
        private long money;
        private String casinoId;
    }

    @Data
    public static class ReceiveMoney {
        private long money;
        private String casinoId;
    }

    @Data
    public static class Game {
        private String id;
        private String token;
    }

    @Data
    public static class Command {
        private String username;
        private String sdate = DateUtils.format(DateUtils.beforeDays(1), "yyyy.MM.dd");

        private String edate = DateUtils.format(new Date(), "yyyy.MM.dd");
    }
}
