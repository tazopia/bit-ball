package spoon.gameZone.dari.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.dari.Dari;
import spoon.support.web.AjaxResult;

public interface DariGameService {

    /**
     * 다리다리 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 다리다리 게임 베팅을 클로징 한다.
     */
    void closingBetting(Dari dari);

    /**
     * 다리다리 게임을 롤백 한다.
     */
    void rollbackPayment(Dari dari);
}
