package spoon.customer.domain;

import lombok.Data;

public class PopupDto {

    @Data
    public static class Add {
        private String summary;
        private int sort;
        private boolean enabled;
    }

    @Data
    public static class Update {
        private Long id;
        private String summary;
        private int sort;
        private boolean enabled;
    }
}
