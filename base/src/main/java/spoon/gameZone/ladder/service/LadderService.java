package spoon.gameZone.ladder.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.ladder.Ladder;
import spoon.gameZone.ladder.LadderConfig;
import spoon.gameZone.ladder.LadderDto;
import spoon.support.web.AjaxResult;

public interface LadderService {

    /**
     * 사다리 설정을 변경한다.
     */
    boolean updateConfig(LadderConfig ladderConfig);

    /**
     * 사다리 등록된 게임을 가져온다.
     */
    Iterable<Ladder> getComplete();

    /**
     * 사다리 종료된 게임을 가져온다.
     */
    Page<Ladder> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 사다리 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    LadderDto.Score findScore(Long id);

    /**
     * 사다리 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(LadderDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    LadderDto.Config gameConfig();

}
