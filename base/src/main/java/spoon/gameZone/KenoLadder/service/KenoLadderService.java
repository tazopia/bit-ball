package spoon.gameZone.KenoLadder.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.KenoLadder.KenoLadder;
import spoon.gameZone.KenoLadder.KenoLadderConfig;
import spoon.gameZone.KenoLadder.KenoLadderDto;
import spoon.gameZone.KenoLadder.KenoLadderScore;
import spoon.gameZone.ZoneDto;
import spoon.support.web.AjaxResult;

public interface KenoLadderService {

    /**
     * 사다리 설정을 변경한다.
     */
    boolean updateConfig(KenoLadderConfig kenoLadderConfig);

    /**
     * 사다리 등록된 게임을 가져온다.
     */
    Iterable<KenoLadder> getComplete();

    /**
     * 사다리 종료된 게임을 가져온다.
     */
    Page<KenoLadder> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 사다리 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    KenoLadderDto.Score findScore(Long id);

    /**
     * 사다리 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(KenoLadderDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    KenoLadderDto.Config gameConfig();

    Iterable<KenoLadderScore> getScore();
}
