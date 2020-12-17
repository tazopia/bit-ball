package spoon.sun.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class SunDto {

    @Data
    public static class SendMoney {
        private long money;
        private int gameNo;
    }

    @Data
    public static class ReceiveMoney {
        private long money;
        private int gameNo;
    }

    @Data
    public static class Command {
        private String sdate = DateUtils.format(DateUtils.beforeDays(1), "yyyy.MM.dd");

        private String edate = DateUtils.format(new Date(), "yyyy.MM.dd");
    }
}
