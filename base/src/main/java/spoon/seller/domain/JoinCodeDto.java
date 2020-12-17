package spoon.seller.domain;

import lombok.Data;

public class JoinCodeDto {

    @Data
    public static class Command {
        private String joinCode;
        private String agency = "-";
    }

    @Data
    public static class Update {
        private Long id;
        private String memo;
        private boolean enabled;
    }

    @Data
    public static class Add {
        private String code;
        private String agency1;
        private String agency2;
        private String agency3;
        private String agency4;
        private String memo;
        private boolean enabled;
    }

    @Data
    public static class Partner {
        private String agency;
        private String role;
    }

}
