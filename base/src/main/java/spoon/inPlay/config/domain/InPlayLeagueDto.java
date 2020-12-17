package spoon.inPlay.config.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

public class InPlayLeagueDto {

    @Data
    public static class Command {
        private String sports;
        private String name;
    }

    @Data
    public static class Update {
        private String name;
        private String korName;
        private MultipartFile file;
    }
}
