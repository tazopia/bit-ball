package spoon.gameZone.soccer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.soccer.Soccer;
import spoon.gameZone.soccer.SoccerConfig;
import spoon.gameZone.soccer.SoccerDto;
import spoon.support.web.AjaxResult;

import java.util.List;

public interface SoccerService {

    /**
     * 가상축구 설정을 변경한다.
     */
    boolean updateConfig(SoccerConfig soccerConfig);

    /**
     * 가상축구 등록된 게임을 가져온다.
     */
    Iterable<Soccer> getComplete();

    /**
     * 가상축구 종료된 게임을 가져온다.
     */
    Page<Soccer> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 가상축구 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    SoccerDto.Score findScore(Long id);

    /**
     * 가상축구 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(SoccerDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    SoccerDto.Config gameConfig();

    List<SoccerDto.List> gameList();

    List<SoccerDto.List> gameList(Long id);
}