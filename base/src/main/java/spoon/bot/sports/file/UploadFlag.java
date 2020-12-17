package spoon.bot.sports.file;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class UploadFlag {

    private String sports;

    private String league;

    private String url;

    private String filename;

    private String ext;

    private boolean empty;

    public UploadFlag(String sports, String league, String url) {
        this.sports = sports;
        this.league = league;
        this.url = url;

        if (StringUtils.isEmpty(url)) {
            empty = true;
            return;
        }

        if (url.contains(("?"))) url = url.substring(0, url.indexOf("?"));
        filename = StringUtils.getFilename(url);
        ext = StringUtils.getFilenameExtension(url);
    }

    public static UploadFlag of(String sports, String league, String url) {
        return new UploadFlag(sports, league, url);
    }

}
