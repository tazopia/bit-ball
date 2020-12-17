package spoon.inPlay.odds.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.odds.domain.Event;
import spoon.inPlay.odds.domain.InPlay;
import spoon.inPlay.odds.domain.InPlayDataDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InPlayEventListener {

    private final InPlayService inPlayService;

    private final InPlayGameService inPlayGameService;

    @EventListener
    public void inPlayListener(InPlayDataDto play) {
        String json = play.getJson();

        try {
            InPlay inPlay = JsonUtils.toModel(json, InPlay.class);
            if (inPlay == null) return;
            int type = inPlay.getHeader().getType();
            List<Event> events;

            switch (type) {
                case 1:
                    log.debug("==================================================================");
                    log.debug("경기 정보를 업데이트 합니다.");
                    log.debug("{}", json);
                    log.debug("==================================================================");
                    events = inPlay.getBody().getEvents();
                    inPlayService.game(events);
                    break;
                case 2:
                    log.debug("==================================================================");
                    log.debug("스코어 정보를 업데이트 합니다.");
                    log.debug("{}", json);
                    log.debug("==================================================================");
                    events = inPlay.getBody().getEvents();
                    inPlayService.score(events);
                    break;
                case 3: // 베팅 진행
                case 35: // 베팅 결과 Settlement 에 당첨 미당첨 적특이 담겨져 나옴
                    log.debug("==================================================================");
                    log.debug("======= 배당 정보 : {}", type);
                    log.debug("{}", json);
                    log.debug("==================================================================");
                    events = inPlay.getBody().getEvents();
                    inPlayService.odds(events);
                    break;
            }
        } catch (RuntimeException e) {
            log.error("==================================================================");
            log.error("{}", json);
            log.error("==================================================================");
        }
    }

    public void gameList() {
        String json = HttpParsing.getJson(InPlayConfig.getGameUrl());
        if (json == null) return;

        // 없는 경기만 갱신한다.
        JsonUtils.toInPlayList(json).stream()
                .filter(game -> !inPlayGameService.exists(game.getFixtureId()))
                .forEach(x -> {
                    inPlayGameService.updateGameInfo(x);
                    inPlayGameService.save(x);
                });
    }
}
