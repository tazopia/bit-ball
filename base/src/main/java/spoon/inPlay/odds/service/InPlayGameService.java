package spoon.inPlay.odds.service;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.entity.InPlayLeague;
import spoon.inPlay.config.entity.InPlaySports;
import spoon.inPlay.config.entity.InPlayTeam;
import spoon.inPlay.odds.domain.Event;
import spoon.inPlay.odds.domain.InPlayDto;
import spoon.inPlay.odds.entity.InPlayGame;
import spoon.inPlay.odds.repository.InPlayGameRepository;
import spoon.mapper.InPlayMapper;

import java.util.List;


@RequiredArgsConstructor
@Service
public class InPlayGameService {

    private final ApplicationEventPublisher eventPublisher;

    private final InPlayMapper inPlayMapper;

    private final InPlayGameRepository inPlayGameRepository;

    public List<InPlayDto.GameData> list() {
        return inPlayMapper.getActiveList();
    }

    public List<InPlayDto.AdminGameData> adminList() {
        return inPlayMapper.getAdminList();
    }

    @Transactional
    public void save(Event event) {
        String sports = event.getFixture().getSports().getName();
        String league = event.getFixture().getLeague().getName();
        String location = event.getFixture().getLocation().getName();
        String hname = event.getFixture().hname();
        String aname = event.getFixture().aname();


        InPlayGame game = InPlayGame.builder()
                .fixtureId(event.getFixtureId())
                .sname(sports)
                .location(location)
                .league(league)
                .sdate(event.getFixture().getStartDate())
                .status(event.getFixture().getStatus())
                .hname(hname)
                .aname(aname)
                .build();
        updateGameInfo(game);


        inPlayGameRepository.save(game);
    }

    public void updateGameInfo(InPlayGame game) {
        // 스포츠 확인
        if (!InPlayConfig.getSports().containsKey(game.getSname())) {
            eventPublisher.publishEvent(new InPlaySports(game.getSname(), "sports.png"));
        }

        // 리그 확인
        if (!InPlayConfig.getLeague().containsKey(game.getLeague())) {
            eventPublisher.publishEvent(new InPlayLeague(game.getLeague(), game.getSname(), game.getLocation(), "league.gif"));
        }

        // 홈팀명 확인
        if (!InPlayConfig.getTeam().containsKey(game.getHname())) {
            eventPublisher.publishEvent(new InPlayTeam(game.getHname(), game.getSname(), game.getLeague()));
        }

        // 원정팀명 확인
        if (!InPlayConfig.getTeam().containsKey(game.getAname())) {
            eventPublisher.publishEvent(new InPlayTeam(game.getAname(), game.getSname(), game.getLeague()));
        }
    }

    public boolean exists(Long fixtureId) {
        return inPlayGameRepository.exists(fixtureId);
    }

    @Transactional
    public void save(InPlayGame game) {
        inPlayGameRepository.save(game);
    }

    public InPlayGame getOne(long fixtureId) {
        return inPlayGameRepository.findOne(fixtureId);
    }


}
