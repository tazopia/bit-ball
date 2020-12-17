package spoon.gameZone.luck.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.luck.Luck;
import spoon.gameZone.luck.LuckConfig;
import spoon.gameZone.luck.LuckDto;
import spoon.support.web.AjaxResult;

public interface LuckService {

    /**
     * 세븐럭 설정을 변경한다.
     */
    boolean updateConfig(LuckConfig luckConfig);

    /**
     * 세븐럭 등록된 게임을 가져온다.
     */
    Iterable<Luck> getComplete();

    /**
     * 세븐럭 종료된 게임을 가져온다.
     */
    Page<Luck> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 세븐럭 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    LuckDto.Score findScore(Long id);

    /**
     * 세븐럭 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(LuckDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    LuckDto.Config gameConfig();

}
