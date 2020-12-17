package spoon.gameZone.aladdin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.aladdin.Aladdin;
import spoon.gameZone.aladdin.AladdinConfig;
import spoon.gameZone.aladdin.AladdinDto;
import spoon.support.web.AjaxResult;

public interface AladdinService {

    /**
     * 알라딘 설정을 변경한다.
     */
    boolean updateConfig(AladdinConfig aladdinConfig);

    /**
     * 알라딘 등록된 게임을 가져온다.
     */
    Iterable<Aladdin> getComplete();

    /**
     * 알라딘 종료된 게임을 가져온다.
     */
    Page<Aladdin> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 알라딘 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    AladdinDto.Score findScore(Long id);

    /**
     * 알라딘 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(AladdinDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    AladdinDto.Config gameConfig();

}
