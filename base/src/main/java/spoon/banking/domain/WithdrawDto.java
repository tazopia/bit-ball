package spoon.banking.domain;

import lombok.Data;
import spoon.config.domain.Config;

public class WithdrawDto {

    @Data
    public static class Amount {
        private int min = Config.getSiteConfig().getPoint().getWithdrawMin();
        private int unit = Config.getSiteConfig().getPoint().getWithdrawUnit();
    }

    @Data
    public static class Add {
        private String bank;
        private String depositor;
        private String account;
        private String bankPassword;
        private long amount;
    }

}
