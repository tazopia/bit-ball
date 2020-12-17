package spoon.support.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
public class FileUploadHandler {

    private ServletContext servletContext;

    public String saveFile(MultipartFile file, String fileName, UploadType target) {

        String path = servletContext.getRealPath(servletContext.getContextPath()).replaceAll("\\\\", "/");
        File transferFile = new File(path + getTargetPath(target) + fileName);
        try {
            if (!file.isEmpty()) {
                file.transferTo(transferFile);
            }
        } catch (IllegalStateException | IOException e) {
            log.error("파일 업로드에 실패하였습니다. - {}, {}", fileName, target);
        }
        return transferFile.getName();
    }

    private String getTargetPath(UploadType target) {
        switch (target) {
            case SPORTS:
                return "images/sports/";
            case LEAGUE:
                return "images/league/";
            case NOTICE:
                return "images/notice/";
            case INPLAY_SPORTS:
                return "images/inplay/sports/";
            case INPLAY_LEAGUE:
                return "images/inplay/league/";
        }
        throw new RuntimeException();
    }

}
