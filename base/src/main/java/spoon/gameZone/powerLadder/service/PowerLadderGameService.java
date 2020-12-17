package spoon.gameZone.powerLadder.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.powerLadder.PowerLadder;
import spoon.support.web.AjaxResult;

public interface PowerLadderGameService {

    /**
     * 파워사다리 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 파워사다리 게임 베팅을 클로징 한다.
     */
    void closingBetting(PowerLadder powerLadder);

    /**
     * 파워사다리 게임을 롤백 한다.
     */
    void rollbackPayment(PowerLadder powerLadder);
}
