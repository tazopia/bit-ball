package spoon.gate.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class GateDto {

    @Data
    public static class Command {
        private String sdate = DateUtils.format(DateUtils.beforeDays(1), "yyyy.MM.dd");

        private String edate = DateUtils.format(new Date(), "yyyy.MM.dd");
    }

}
