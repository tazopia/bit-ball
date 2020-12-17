package spoon.gameZone.lowhi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.lowhi.Lowhi;
import spoon.gameZone.lowhi.LowhiConfig;
import spoon.gameZone.lowhi.LowhiDto;
import spoon.support.web.AjaxResult;

public interface LowhiService {

    /**
     * 로하이 설정을 변경한다.
     */
    boolean updateConfig(LowhiConfig lowhiConfig);

    /**
     * 로하이 등록된 게임을 가져온다.
     */
    Iterable<Lowhi> getComplete();

    /**
     * 로하이 종료된 게임을 가져온다.
     */
    Page<Lowhi> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 로하이 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    LowhiDto.Score findScore(Long id);

    /**
     * 로하이 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(LowhiDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    LowhiDto.Config gameConfig();

}
