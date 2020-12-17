package spoon.board.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.bet.entity.Bet;
import spoon.board.entity.Board;
import spoon.common.utils.DateUtils;
import spoon.member.domain.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class BoardDto {

    @Data
    public static class Command {
        private Long id;
        private String code;
        private String searchType = "";
        private String searchValue = "";
        private String username;
        private boolean match;
    }

    @Data
    public static class Add {
        private String userid;
        private String betId;
        private String title;
        private String contents;
        private String code;
        private Iterable<Bet> bets = new ArrayList<>();
    }

    @Data
    public static class AdminAdd {
        private String date;
        private String hour;
        private String minute;
        private String second;
        private String code;
        private String userid;
        private String betId;
        private int hit;
        private String title;
        private String contents;
        private boolean showTop;

        public AdminAdd() {
            Date regDate = new Date();
            this.date = DateUtils.format(regDate, "yyyy.MM.dd");
            this.hour = DateUtils.format(regDate, "HH");
            this.minute = DateUtils.format(regDate, "mm");
            this.second = DateUtils.format(regDate, "ss");
        }

        public Date getRegDate() {
            String date = this.date + this.hour + this.minute + this.second;
            Date regDate = DateUtils.parse(date, "yyyy.MM.ddHHmmss");
            return regDate == null ? new Date() : regDate;
        }
    }

    @NoArgsConstructor
    @Data
    public static class AdminUpdate {
        private Long id;
        private String date;
        private String hour;
        private String minute;
        private String second;
        private String code;
        private String userid;
        private String betId;
        private int hit;
        private String title;
        private String contents;
        private boolean showTop;

        public AdminUpdate(Board board) {
            this.date = DateUtils.format(board.getRegDate(), "yyyy.MM.dd");
            this.hour = DateUtils.format(board.getRegDate(), "HH");
            this.minute = DateUtils.format(board.getRegDate(), "mm");
            this.second = DateUtils.format(board.getRegDate(), "ss");
            this.id = board.getId();
            this.code = board.getCode();
            this.userid = board.getRole() == Role.ADMIN ? "_ADMIN_" : board.getUserid();
            this.betId = board.getBetId() == null ? "" : Arrays.stream(board.getBetId()).mapToObj(String::valueOf).collect(Collectors.joining(","));
            this.hit = board.getHit();
            this.title = board.getTitle();
            this.contents = board.getContents();
            this.showTop = board.isShowTop();
        }

        public Date getRegDate() {
            String date = this.date + this.hour + this.minute + this.second;
            Date regDate = DateUtils.parse(date, "yyyy.MM.ddHHmmss");
            return regDate == null ? new Date() : regDate;
        }
    }

    @Data
    public static class Hidden {
        private Long id;
        private boolean hidden;
    }

    @Data
    public static class Show {
        private Long id;
        private boolean showTop;
    }

    @Data
    public static class Main {
        private Long id;
        private String code;
        private String title;
    }

}
