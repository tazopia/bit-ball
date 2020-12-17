package spoon.game.service;

import spoon.game.domain.GameDto;
import spoon.game.entity.Game;

import java.util.Date;
import java.util.List;

public interface GameBotService {

    /**
     * 게임봇에서 검색된 봇게임을 Game 형식으로 변환한 뒤 저장한다.
     */
    boolean addGame(Game game);

    /**
     * 게임봇에서 가져온 게임을 업데이트 한다.
     */
    boolean updateGame(Game game);

    /**
     * 게임봇에서 게임의 스코어를 입력한다.
     */
    boolean gameScore(Long gameId, Integer scoreHome, Integer scoreAway, boolean cancel, String ut);

    /**
     * siteCode, siteId 로 게임이 존재하는지 판단한다.
     */
    boolean isExist(String siteCode, String siteId);

    /**
     * siteCode, siteId 로 게임 한개를 가져온다.
     */
    Game getGame(String siteCode, String siteId);

    /**
     * 현재 등록된 경기의 ut (업데이트 타임)을 가져온다.
     */
    String getMaxUt(String siteCode, boolean closing);

    /**
     * 그룹 아이디를 찾아라
     */
    String findGroupId(String siteId, String siteCode, Date gameDate, String league, String teamHome, String teamAway);


    List<GameDto.Bot> initOdds(String siteCode);

    List<GameDto.Bot> initScore(String siteCode);
}
