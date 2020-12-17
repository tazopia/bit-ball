package spoon.bet.service;

import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.gameZone.ZoneScore;

public interface BetClosingService {

    /**
     * 스포츠 경기에 베팅한 유저가 있는지 판단
     */
    boolean notBetting(long gameId);

    /**
     * 스포츠 경기를 종료 처리 한다.
     */
    void closingGameBetting(Long gameId, Integer scoreHome, Integer scoreAway, boolean cancel);

    /**
     * 스포츠 경기를 롤백한다.
     */
    void rollbackBetting(Long gameId);

    /**
     * 베팅을 롤백한다.
     */
    void rollbackBetting(Iterable<BetItem> betItems);

    /**
     * 게임존 경기를 종료 처리 한다.
     */
    void closingZoneBetting(Bet bet, ZoneScore zoneScore);

}
