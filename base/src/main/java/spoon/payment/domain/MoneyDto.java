package spoon.payment.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class MoneyDto {

    @Data
    public static class SellerCommand {
        private String role;
        private String agency;

        private String userid;
        private String code;
        private String sdate = DateUtils.format(new Date(), "yyyy.MM.dd");
        private String edate = DateUtils.format(new Date(), "yyyy.MM.dd");

        public Date getStart() {
            return DateUtils.start(this.sdate);
        }

        public Date getEnd() {
            return DateUtils.end(this.edate);
        }
    }

}
