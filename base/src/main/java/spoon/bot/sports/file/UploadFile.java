package spoon.bot.sports.file;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UploadFile {

    private MultipartFile file;

    private String name;

    private TargetCode target;

    public boolean isEmpty() {
        return file == null || file.isEmpty();
    }

    public String getFilename() {
        return this.isEmpty() ? null : this.name + "." + this.getExt();
    }

    private String getExt() {
        return Optional.ofNullable(StringUtils.getFilenameExtension(file.getOriginalFilename())).orElse("").toLowerCase();
    }

    public static UploadFile of(MultipartFile file, String fileName, TargetCode target) {
        return new UploadFile(file, fileName, target);
    }

    public static UploadFile of(MultipartFile file, long id, TargetCode target) {
        return new UploadFile(file, target.name().toLowerCase() + "-" + String.format("%04d", id), target);
    }
}
