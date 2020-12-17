package spoon.gameZone.KenoLadder.service;

import spoon.gameZone.KenoLadder.KenoLadder;
import spoon.gameZone.ZoneDto;
import spoon.support.web.AjaxResult;

public interface KenoLadderGameService {

    /**
     * 사다리 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 사다리 게임 베팅을 클로징 한다.
     */
    void closingBetting(KenoLadder ladder);

    /**
     * 사다리 게임을 롤백 한다.
     */
    void rollbackPayment(KenoLadder ladder);
}
