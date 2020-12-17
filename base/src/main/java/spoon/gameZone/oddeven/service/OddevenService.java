package spoon.gameZone.oddeven.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.oddeven.Oddeven;
import spoon.gameZone.oddeven.OddevenConfig;
import spoon.gameZone.oddeven.OddevenDto;
import spoon.support.web.AjaxResult;

public interface OddevenService {

    /**
     * 홀짝 설정을 변경한다.
     */
    boolean updateConfig(OddevenConfig oddevenConfig);

    /**
     * 홀짝 등록된 게임을 가져온다.
     */
    Iterable<Oddeven> getComplete();

    /**
     * 홀짝 종료된 게임을 가져온다.
     */
    Page<Oddeven> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 홀짝 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    OddevenDto.Score findScore(Long id);

    /**
     * 홀짝 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(OddevenDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    OddevenDto.Config gameConfig();

}
