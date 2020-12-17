package spoon.gameZone.powerLadder.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.powerLadder.PowerLadder;
import spoon.gameZone.powerLadder.PowerLadderConfig;
import spoon.gameZone.powerLadder.PowerLadderDto;
import spoon.gameZone.powerLadder.PowerLadderScore;
import spoon.support.web.AjaxResult;

public interface PowerLadderService {

    /**
     * 사다리 설정을 변경한다.
     */
    boolean updateConfig(PowerLadderConfig powerLadderConfig);

    /**
     * 사다리 등록된 게임을 가져온다.
     */
    Iterable<PowerLadder> getComplete();

    /**
     * 사다리 종료된 게임을 가져온다.
     */
    Page<PowerLadder> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 사다리 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    PowerLadderDto.Score findScore(Long id);

    /**
     * 사다리 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(PowerLadderDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    PowerLadderDto.Config gameConfig();

    Iterable<PowerLadderScore> getScore();
}
