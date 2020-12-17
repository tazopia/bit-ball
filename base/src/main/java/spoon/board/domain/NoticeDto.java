package spoon.board.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.board.entity.Notice;
import spoon.common.utils.DateUtils;

import java.util.Date;

public class NoticeDto {

    @Data
    public static class Add {
        private String date;
        private String hour;
        private String minute;
        private String title;
        private boolean enabled;

        public Add() {
            Date regDate = new Date();
            this.date = DateUtils.format(regDate, "yyyy.MM.dd");
            this.hour = DateUtils.format(regDate, "HH");
            this.minute = DateUtils.format(regDate, "mm");
        }

        public Date getRegDate() {
            String date = this.date + this.hour + this.minute;
            Date regDate = DateUtils.parse(date, "yyyy.MM.ddHHmm");
            return regDate == null ? new Date() : regDate;
        }
    }

    @NoArgsConstructor
    @Data
    public static class Update {
        private Long id;
        private String date;
        private String hour;
        private String minute;
        private String title;
        private boolean enabled;

        public Update(Notice notice) {
            this.date = DateUtils.format(notice.getRegDate(), "yyyy.MM.dd");
            this.hour = DateUtils.format(notice.getRegDate(), "HH");
            this.minute = DateUtils.format(notice.getRegDate(), "mm");
            this.id = notice.getId();
            this.title = notice.getTitle();
            this.enabled = notice.isEnabled();
        }

        public Date getRegDate() {
            String date = this.date + this.hour + this.minute;
            Date regDate = DateUtils.parse(date, "yyyy.MM.ddHHmm");
            return regDate == null ? new Date() : regDate;
        }
    }
}
