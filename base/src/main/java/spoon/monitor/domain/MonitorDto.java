package spoon.monitor.domain;

import lombok.Data;
import spoon.game.domain.MenuCode;

public class MonitorDto {

    @Data
    public static class Amount {
        private long money;
        private long point;
    }

    @Data
    public static class Bank {
        private long inAmount;
        private long outAmount;
    }

    @Data
    public static class Bet {
        private MenuCode menuCode;
        private long betMoney;
        private long hitMoney;
    }
}
