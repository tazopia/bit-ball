package spoon.board.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class CommentDto {

    @Data
    public static class Add {
        private Long boardId;
        private String code;
        private String contents;
        private int page;
    }

    @Data
    public static class AdminAdd {
        private Long boardId;
        private String code;
        private String date;
        private String hour;
        private String minute;
        private String second;
        private String userid;
        private String contents;

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

    @Data
    public static class Hidden {
        private Long id;
        private boolean hidden;
    }
}
