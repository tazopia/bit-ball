package spoon.inPlay.config.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import spoon.common.utils.StringUtils;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.domain.InPlayTeamDto;
import spoon.inPlay.config.entity.InPlayTeam;
import spoon.inPlay.config.entity.QInPlayTeam;
import spoon.inPlay.config.repository.InPlayTeamRepository;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
public class InPlayTeamService {

    private final InPlayTeamRepository inPlayTeamRepository;

    private static final QInPlayTeam TEAM = QInPlayTeam.inPlayTeam;

    @PostConstruct
    public void init() {
        inPlayTeamRepository.findAll().forEach(x -> InPlayConfig.getTeam().put(x.getName(), x));
    }

    /**
     * @Async 를 사용하여 쓰레드 1개만을 사용하도록 한다.
     */
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addTeam(InPlayTeam team) {
        boolean exist = inPlayTeamRepository.exists(team.getName());
        if (exist) {
            if (!InPlayConfig.getTeam().containsKey(team.getName())) {
                team = inPlayTeamRepository.findOne(team.getName());
                InPlayConfig.getTeam().put(team.getName(), team);
            }
            return;
        }

        inPlayTeamRepository.save(team);
        InPlayConfig.getTeam().put(team.getName(), team);
    }

    public Page<InPlayTeam> getPage(Pageable pageable, InPlayTeamDto.Command command) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getSports())) {
            builder.and(TEAM.sports.eq(command.getSports()));
        }

        if (StringUtils.notEmpty(command.getName())) {
            builder.and(
                    TEAM.korName.contains(command.getName())
                            .or(TEAM.name.contains(command.getName()))
                            .or(TEAM.league.contains(command.getName()))
            );
        }

        return inPlayTeamRepository.findAll(builder, pageable);
    }

    @Transactional
    public void update(InPlayTeamDto.Update update) {
        InPlayTeam team = inPlayTeamRepository.findOne(update.getName());
        team.updateKorName(update.getKorName());
        InPlayConfig.getTeam().put(team.getName(), team);
    }

    public InPlayTeam findOne(String name) {
        return inPlayTeamRepository.findOne(name);
    }
}
