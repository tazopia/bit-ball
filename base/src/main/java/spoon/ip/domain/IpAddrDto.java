package spoon.ip.domain;

import lombok.Data;

public class IpAddrDto {

    @Data
    public static class Command {
        private String ip;
        private String code;
    }

    @Data
    public static class Add {
        public String ip;
        public String code;
        public String memo;
    }
}
