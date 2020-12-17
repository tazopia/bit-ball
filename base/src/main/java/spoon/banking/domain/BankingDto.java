package spoon.banking.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;

public class BankingDto {

    @Data
    public static class Command {
        private BankingCode bankingCode;
        private String date;
        private boolean closing;
        private String username;
        private boolean match;
        private String depositor;
    }

    @Data
    public static class Seller {
        private String bankingCode;
        private String agency;
        private String role;
        private String username;
        private boolean match;
    }

    @Data
    public static class Date {
        private String userid;
        private String sdate = DateUtils.format(DateUtils.beforeDays(7));
        private String edate = DateUtils.format(new java.util.Date());

        public String getStart() {
            return this.sdate.replaceAll("\\.", "-");
        }

        public String getEnd() {
            return this.edate.replaceAll("\\.", "-");
        }
    }

    @Data
    public static class Money {
        private long deposit;
        private long withdraw;
    }
}
