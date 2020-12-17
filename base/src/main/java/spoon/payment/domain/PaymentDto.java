package spoon.payment.domain;

import lombok.Data;

public class PaymentDto {

    @Data
    public static class Command {
        private String userid;
        private String username;
        private boolean match;
        private String code;
    }

    @Data
    public static class Add {
        private String userid;
        private String memo;
        private boolean plus;
        private long amount;
    }

}
