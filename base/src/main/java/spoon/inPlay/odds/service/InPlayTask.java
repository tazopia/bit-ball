package spoon.inPlay.odds.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;

@RequiredArgsConstructor
@Component
public class InPlayTask {

    private final InPlayEventListener inPlayEventListener;

    private final InPlayOddsService inPlayOddsService;

    /**
     * 1분 단위로 서버로부터 경기 목록을 받아오는 데몬을 crontab 에 설치
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void getGameList() {
        if (!Config.getSysConfig().getSports().isCanInplay()) return;
        inPlayEventListener.gameList();
    }

    /**
     * 1분 단위로 메모리 디비에서 실 디비로 배당을 옮김
     */
    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 10 * 1000)
    public void updateOdds() {
        if (!Config.getSysConfig().getSports().isCanInplay()) return;
        inPlayOddsService.updateOdds();
    }

    /**
     * 경기가 끝났으나 5시간 이상 종료되지 않은 배당은 -1 (최소처리) 한다
     * 수동으로 정산을 원하면 이부분을 주석처리 하고 수동처리 하면 된다.
     */
    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 10 * 1000)
    public void cancelOdds() {
        if (!Config.getSysConfig().getSports().isCanInplay()) return;
        inPlayOddsService.cancelOdds();
    }
}
