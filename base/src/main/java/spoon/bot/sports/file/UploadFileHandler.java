package spoon.bot.sports.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
public class UploadFileHandler {

    private static String CONTEXT_PATH;

    public UploadFileHandler(ServletContext servletContext) {
        CONTEXT_PATH = servletContext.getRealPath(servletContext.getContextPath()).replaceAll("\\\\", "/");
    }

    /**
     * MultipartFile 을 업로드 합니다.
     *
     * @param file UploadFile has MultipartFile
     */
    void saveFile(UploadFile file) {
        String filename = CONTEXT_PATH + file.getTarget().getPath() + file.getFilename();
        File transferFile = new File(filename);

        try {
            file.getFile().transferTo(transferFile);
        } catch (IOException e) {
            log.error("파일 업로드에 실패하였습니다.", e);
        }
    }


    void saveFile(UploadFlag flag) {
        String filename = CONTEXT_PATH + "images/league/" + flag.getFilename();
        File file = new File(filename);

        try {
            ImageIO.write(ImageIO.read(new URL(flag.getUrl())), flag.getExt(), file);
        } catch (IOException e) {
            log.error("파일 업로드에 실패하였습니다.", e);
        }
    }
}
