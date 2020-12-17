package spoon.gameZone.crownOddeven.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.crownOddeven.CrownOddeven;
import spoon.gameZone.crownOddeven.CrownOddevenConfig;
import spoon.gameZone.crownOddeven.CrownOddevenDto;
import spoon.support.web.AjaxResult;

public interface CrownOddevenService {

    /**
     * 홀짝 설정을 변경한다.
     */
    boolean updateConfig(CrownOddevenConfig crownOddevenConfig);

    /**
     * 홀짝 등록된 게임을 가져온다.
     */
    Iterable<CrownOddeven> getComplete();

    /**
     * 홀짝 종료된 게임을 가져온다.
     */
    Page<CrownOddeven> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 홀짝 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    CrownOddevenDto.Score findScore(Long id);

    /**
     * 홀짝 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(CrownOddevenDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    CrownOddevenDto.Config gameConfig();

}
