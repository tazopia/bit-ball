package spoon.sale.domain;

import lombok.Data;

public class SaleDto {

    @Data
    public static class Command {
        private String agency1;
        private String agency2;
        private String agency3;
        private String agency4;
    }

    @Data
    public static class Payment {
        private String userid;
        private Long saleId;
        private long amount;
    }

    @Data
    public static class AgencyCommand {
        private String role;
        private String agency;
    }

    @Data
    public static class SaleCommand {
        private String agency1;
        private String agency2;
        private String agency3;
        private String agency4;

        private String role;
        private String agency;
    }
}
