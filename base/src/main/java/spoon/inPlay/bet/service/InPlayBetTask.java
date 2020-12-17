package spoon.inPlay.bet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.config.domain.Config;

@RequiredArgsConstructor
@Component
public class InPlayBetTask {

    private final InPlayBetService inPlayBetService;

    /**
     * 1분 단위로 서버로부터 경기 목록을 받아오는 데몬을 crontab 에 설치
     */
    @Scheduled(fixedDelay = 10 * 1000)
    public void getGameList() {
        inPlayBetService.checkBetList();
    }

    /**
     * 1분 단위로 메모리 디비에서 실 디비로 배당을 옮김
     */
    @Scheduled(fixedDelay = 30 * 1000, initialDelay = 20 * 1000)
    public void updateOdds() {
        if (!Config.getSysConfig().getSports().isCanInplay()) return;
        inPlayBetService.score();
    }

}
