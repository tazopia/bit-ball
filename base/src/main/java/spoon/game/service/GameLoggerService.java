package spoon.game.service;

import spoon.game.entity.GameLogger;

import java.util.Date;

public interface GameLoggerService {

    /**
     * GameLogger 를 등록한다.
     */
    void addGameLogger(GameLogger logger);

    /**
     * 로그 내역을 삭제한다.
     */
    void deleteGameLogger(long gameId);

    /**
     * Game.id 로 GameLogger 를 가져온다.
     */
    Iterable<GameLogger> getGameLogger(long gameId);

    /**
     * 게임의 경기시간이 변경 되었는지 확인한다.
     */
    boolean isChangeDate(long id, Date gameDate);
}
