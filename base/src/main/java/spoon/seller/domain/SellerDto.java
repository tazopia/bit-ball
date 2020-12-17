package spoon.seller.domain;

import lombok.Data;

public class SellerDto {

    @Data
    public static class Command {
        private String agency1;
        private String agency2;
        private String agency3;
        private String agency4;
    }

    @Data
    public static class AgencyCommand {
        private String role;
        private String agency;
    }

    @Data
    public static class Update {
        private String userid;

        private double powerMax;
        private double powerMin;

        private double powerLadder;
        private double keno;
        private double kenoLadder;

        private double btc3Max;
        private double btc3Min;
        private double btc5Max;
        private double btc5Min;
    }
}
