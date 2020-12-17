package spoon.inPlay.config.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import spoon.common.utils.StringUtils;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.domain.InPlayLeagueDto;
import spoon.inPlay.config.entity.InPlayLeague;
import spoon.inPlay.config.entity.QInPlayLeague;
import spoon.inPlay.config.repository.InPlayLeagueRepository;
import spoon.support.web.FileUploadHandler;
import spoon.support.web.UploadType;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
public class InPlayLeagueService {

    private final InPlayLeagueRepository inPlayLeagueRepository;

    private final FileUploadHandler fileUploadHandler;

    private static final QInPlayLeague LEAGUE = QInPlayLeague.inPlayLeague;

    @PostConstruct
    public void init() {
        inPlayLeagueRepository.findAll().forEach(x -> InPlayConfig.getLeague().put(x.getName(), x));
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addLeague(InPlayLeague league) {
        boolean exist = inPlayLeagueRepository.exists(league.getName());
        if (exist) {
            if (!InPlayConfig.getLeague().containsKey(league.getName())) {
                league = inPlayLeagueRepository.findOne(league.getName());
                InPlayConfig.getLeague().put(league.getName(), league);
            }
            return;
        }
        inPlayLeagueRepository.save(league);
        InPlayConfig.getLeague().put(league.getName(), league);
    }

    public Page<InPlayLeague> getPage(Pageable pageable, InPlayLeagueDto.Command command) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getSports())) {
            builder.and(LEAGUE.sports.eq(command.getSports()));
        }

        if (StringUtils.notEmpty(command.getName())) {
            builder.and(
                    LEAGUE.korName.contains(command.getName())
                            .or(LEAGUE.name.contains(command.getName()))
                            .or(LEAGUE.location.contains(command.getName()))
            );
        }

        return inPlayLeagueRepository.findAll(builder, pageable);
    }

    public InPlayLeague findOne(String name) {
        return inPlayLeagueRepository.getOne(name);
    }

    @Transactional
    public void update(InPlayLeagueDto.Update update) {
        InPlayLeague league = inPlayLeagueRepository.findOne(update.getName());
        if (update.getFile() != null && update.getFile().getSize() > 0) {
            String saveFileName = fileUploadHandler.saveFile(update.getFile(), update.getName(), UploadType.INPLAY_LEAGUE);
            league.updateFlag(saveFileName);
        }
        league.updateKorName(update.getKorName());
        InPlayConfig.getLeague().put(league.getName(), league);
    }
}
