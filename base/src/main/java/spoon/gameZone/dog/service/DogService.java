package spoon.gameZone.dog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.dog.Dog;
import spoon.gameZone.dog.DogConfig;
import spoon.gameZone.dog.DogDto;
import spoon.support.web.AjaxResult;

import java.util.List;

public interface DogService {

    /**
     * 개경주 설정을 변경한다.
     */
    boolean updateConfig(DogConfig dogConfig);

    /**
     * 개경주 등록된 게임을 가져온다.
     */
    Iterable<Dog> getComplete();

    /**
     * 개경주 종료된 게임을 가져온다.
     */
    Page<Dog> getClosing(ZoneDto.Command command, Pageable pageable);

    /**
     * 개경주 봇에 접속하여 기존 결과가 있는지 확인 한다.
     */
    DogDto.Score findScore(Long id);

    /**
     * 개경주 스코어를 가지고 결과처리를 한다.
     */
    boolean closingGame(DogDto.Score score);

    /**
     * 결과처리가 되지 않고 베팅이 없는 모든 경기를 종료처리 한다.
     */
    AjaxResult closingAllGame();

    /**
     * 현재 진행중인 경기의 설정을 가져온다.
     */
    DogDto.Config gameConfig();

    /**
     * 베팅가능한 모든 경기를 가져온다.
     */
    List<DogDto.List> gameList();

    /**
     * 베팅 가능한 새로 등록된 경기만 가져온다.
     */
    List<DogDto.List> gameList(Long id);
}