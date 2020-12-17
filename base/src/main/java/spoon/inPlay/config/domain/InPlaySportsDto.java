package spoon.inPlay.config.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

public class InPlaySportsDto {

    @Data
    public static class Update {
        private String name;
        private String korName;
        private MultipartFile file;
    }

}
