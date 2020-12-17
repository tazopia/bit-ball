package spoon.gameZone.crownSutda.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.crownSutda.CrownSutda;
import spoon.gameZone.crownSutda.CrownSutdaConfig;
import spoon.gameZone.crownSutda.CrownSutdaDto;
import spoon.support.web.AjaxResult;

public interface CrownSutdaService {

    /**
     * 바카라 설정을 변경한다.
     */
    boolean updateConfig(CrownSutdaConfig crownSutdaConfig);

    /**
     * 바카라 등록된 게임을 가져온다.
     */
    Iterable<CrownSutda> getComplete();

    /**
     * 바카라 종료된 게임을 가져온다.
     */
    Page<CrownSutda> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 바카라 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    CrownSutdaDto.Score findScore(Long id);

    /**
     * 바카라 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(CrownSutdaDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    CrownSutdaDto.Config gameConfig();

}
