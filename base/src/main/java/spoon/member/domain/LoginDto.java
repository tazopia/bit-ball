package spoon.member.domain;

import lombok.Data;

public class LoginDto {

    @Data
    public static class Command {
        private String loginDate;
        private String username;
        private boolean match;
        private String ip;
        private String device;
    }
}
