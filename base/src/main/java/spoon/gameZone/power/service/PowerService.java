package spoon.gameZone.power.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.power.Power;
import spoon.gameZone.power.PowerConfig;
import spoon.gameZone.power.PowerDto;
import spoon.gameZone.power.PowerScore;
import spoon.support.web.AjaxResult;

import java.util.List;

public interface PowerService {

    /**
     * 파워볼 설정을 변경한다.
     */
    boolean updateConfig(PowerConfig powerConfig);

    /**
     * 파워볼 등록된 게임을 가져온다.
     */
    Iterable<Power> getComplete();

    /**
     * 파워볼 종료된 게임을 가져온다.
     */
    Page<Power> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 파워볼 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    PowerDto.Score findScore(Long id);

    /**
     * 파워볼 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(PowerDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    PowerDto.Config gameConfig();

    // 결과를 가져온다.
    List<PowerScore> getScore();
}
