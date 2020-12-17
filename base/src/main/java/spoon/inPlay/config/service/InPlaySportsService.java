package spoon.inPlay.config.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.domain.InPlaySportsDto;
import spoon.inPlay.config.entity.InPlaySports;
import spoon.inPlay.config.repository.InPlaySportsRepository;
import spoon.support.web.FileUploadHandler;
import spoon.support.web.UploadType;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InPlaySportsService {

    private final InPlaySportsRepository inPlaySportsRepository;

    private final FileUploadHandler fileUploadHandler;

    @PostConstruct
    public void init() {
        inPlaySportsRepository.findAll().forEach(x -> InPlayConfig.getSports().put(x.getName(), x));
    }

    public List<InPlaySports> getSports() {
        return inPlaySportsRepository.findAll();
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addSports(InPlaySports sports) {
        boolean exist = inPlaySportsRepository.exists(sports.getName());
        if (exist) {
            if (!InPlayConfig.getSports().containsKey(sports.getName())) {
                sports = inPlaySportsRepository.findOne(sports.getName());
                InPlayConfig.getSports().put(sports.getName(), sports);
            }
            return;
        }
        inPlaySportsRepository.save(sports);
        InPlayConfig.getSports().put(sports.getName(), sports);
    }

    public InPlaySports findOne(String id) {
        return inPlaySportsRepository.findOne(id);
    }

    @Transactional
    public void update(InPlaySportsDto.Update update) {
        InPlaySports sports = inPlaySportsRepository.findOne(update.getName());
        if (update.getFile() != null && update.getFile().getSize() > 0) {
            String saveFileName = fileUploadHandler.saveFile(update.getFile(), update.getName(), UploadType.INPLAY_SPORTS);
            sports.updateFlag(saveFileName);
        }
        sports.updateKorName(update.getKorName());
        InPlayConfig.getSports().put(sports.getName(), sports);
    }
}
