package spoon.customer.domain;

import lombok.Data;
import spoon.config.domain.Config;
import spoon.customer.entity.AutoMemo;

public class AutoMemoDto {

    @Data
    public static class Command {
        private String code;
        private boolean onlyEnabled;
    }

    @Data
    public static class Update {
        private Long id;
        private String name;
        private String title;
        private String contents;
    }

    @Data
    public static class Join {
        private String title;
        private String contents;
        private boolean enabled;
    }

    @Data
    public static class Reply {
        private String title;
        private String contents;

        public Reply(QnaDto.Auto auto) {
            AutoMemo memo = Config.getAutoMemoMap().get(auto.getId());
            this.title = memo.getTitle().replaceAll("#회사#", Config.getSiteConfig().getCompanyName())
                    .replaceAll("#아이디#", auto.getUserid())
                    .replaceAll("#닉네임#", auto.getNickname());
            this.contents = memo.getContents().replaceAll("#회사#", Config.getSiteConfig().getCompanyName())
                    .replaceAll("#아이디#", auto.getUserid())
                    .replaceAll("#닉네임#", auto.getNickname());
        }
    }
}
