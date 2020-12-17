package spoon.banking.domain;

import lombok.Data;
import spoon.config.domain.Config;

public class DepositDto {

    @Data
    public static class Amount {
        private int min = Config.getSiteConfig().getPoint().getDepositMin();
        private int unit = Config.getSiteConfig().getPoint().getDepositUnit();
    }

    @Data
    public static class Add {
        private String depositor;
        private long amount;
    }

}
