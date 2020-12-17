package spoon.customer.domain;

import lombok.Data;
import spoon.bet.entity.Bet;

import java.util.ArrayList;

public class QnaDto {

    @Data
    public static class Command {
        private String username = "";
        private boolean match;
        private String searchType = "";
        private String searchValue = "";
    }

    @Data
    public static class Add {
        private String title;
        private String contents;
        private String betId;
        private Iterable<Bet> bets = new ArrayList<>();
    }

    @Data
    public static class Reply {
        private Long id;
        private String reTitle;
        private String reply;
    }

    @Data
    public static class Auto {
        private Long id;
        private String userid;
        private String nickname;
    }
}
