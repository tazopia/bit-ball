package spoon.gameZone.newSnail.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.newSnail.NewSnail;
import spoon.gameZone.newSnail.NewSnailConfig;
import spoon.gameZone.newSnail.NewSnailDto;
import spoon.support.web.AjaxResult;

public interface NewSnailService {

    /**
     * NEW 달팽이 설정을 변경한다.
     */
    boolean updateConfig(NewSnailConfig newSnailConfig);

    /**
     * NEW 달팽이 등록된 게임을 가져온다.
     */
    Iterable<NewSnail> getComplete();

    /**
     * NEW 달팽이 종료된 게임을 가져온다.
     */
    Page<NewSnail> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * NEW 달팽이 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    NewSnailDto.Score findScore(Long id);

    /**
     * NEW 달팽이 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(NewSnailDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    NewSnailDto.Config gameConfig();

}
