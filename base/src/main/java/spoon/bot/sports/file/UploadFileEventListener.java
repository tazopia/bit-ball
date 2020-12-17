package spoon.bot.sports.file;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import spoon.game.repository.LeagueRepository;

@RequiredArgsConstructor
@Component
public class UploadFileEventListener {

    private final UploadFileHandler fileHandler;

    private final LeagueRepository leagueRepository;

    @Async
    @EventListener
    public void uploadFileEvent(UploadFile file) {
        if (!file.isEmpty())
            fileHandler.saveFile(file);
    }

    @Async
    @EventListener
    public void uploadFlagEvent(UploadFlag flag) {
        if (!flag.isEmpty()) {
            fileHandler.saveFile(flag);
        }
    }

}
