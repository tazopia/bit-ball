package spoon.customer.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

public class MemoDto {

    @Data
    public static class Command {
        private String checked;
        private String username;
        private boolean match;
        private String agency1; // 대리점
        private String agency2; // 총판
        private String agency3; // 대총판
        private String agency4; // 본사총판
        private String regDate;
    }

    @Data
    public static class Add {
        private String agency1; // 대리점
        private String agency2; // 총판
        private String agency3; // 대퐁판
        private String agency4; // 본사총판
        private String title;
        private String contents;
        private String userType = "";
        private int level = 0;

        private String worker;
        private String ip;
    }

    @NoArgsConstructor
    @Data
    public static class One {
        private String userid;
        private String title;
        private String contents;

        public One(String userid) {
            this.userid = userid;
        }
    }
}
