package spoon.inPlay.odds.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.common.utils.JsonUtils;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.odds.domain.Event;
import spoon.inPlay.odds.entity.InPlayScore;
import spoon.inPlay.odds.entity.QInPlayGame;
import spoon.inPlay.odds.entity.QInPlayScore;
import spoon.inPlay.odds.repository.InPlayGameRepository;
import spoon.inPlay.odds.repository.InPlayScoreRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InPlayScoreService {

    private final InPlayGameRepository inPlayGameRepository;

    private final InPlayScoreRepository inPlayScoreRepository;

    private static final QInPlayGame GAME = QInPlayGame.inPlayGame;

    private static final QInPlayScore SCORE = QInPlayScore.inPlayScore;

    /**
     * Map 에 스코어가 있으면 Map 을 리턴하고 없으면 디비에서 검색한다.
     */
    public String getScore(long id) {
        if (InPlayConfig.getScore().containsKey(id)) return InPlayConfig.getScore().get(id);
        String score = Optional.ofNullable(inPlayScoreRepository.findOne(SCORE.fixtureId.eq(id))).map(InPlayScore::getLiveScore).orElse("");
        InPlayConfig.getScore().put(id, score);
        return score;
    }

    @Transactional
    public void save(Event event) {
        long fixtureId = event.getFixtureId();

        // 게임에 status 를 업데이트 한다.
        Optional.ofNullable(inPlayGameRepository.findOne(GAME.fixtureId.eq(fixtureId)))
                .ifPresent(game -> game.updateStatus(event.getLiveScore().getScoreBoard().getStatus()));

        // 스코어맵에 스코어를 기록한다.
        String live = JsonUtils.toString(event.getLiveScore());
        InPlayConfig.getScore().put(fixtureId, live);

        // 스코어를 기록한다.
        InPlayScore score = InPlayScore.builder()
                .fixtureId(fixtureId)
                .liveScore(live)
                .build();
        inPlayScoreRepository.save(score);
    }

    public InPlayScore getOne(long fixtureId) {
        return inPlayScoreRepository.findOne(SCORE.fixtureId.eq(fixtureId));
    }
}
