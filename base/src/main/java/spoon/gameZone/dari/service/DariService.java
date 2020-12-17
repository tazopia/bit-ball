package spoon.gameZone.dari.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.dari.Dari;
import spoon.gameZone.dari.DariConfig;
import spoon.gameZone.dari.DariDto;
import spoon.support.web.AjaxResult;

public interface DariService {

    /**
     * 다리다리 설정을 변경한다.
     */
    boolean updateConfig(DariConfig dariConfig);

    /**
     * 다리다리 등록된 게임을 가져온다.
     */
    Iterable<Dari> getComplete();

    /**
     * 다리다리 종료된 게임을 가져온다.
     */
    Page<Dari> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 다리다리 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    DariDto.Score findScore(Long id);

    /**
     * 다리다리 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(DariDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    DariDto.Config gameConfig();

}
