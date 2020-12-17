package spoon.gameZone.power.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.power.Power;
import spoon.support.web.AjaxResult;

public interface PowerGameService {

    /**
     * 파워볼 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 파워볼 게임 베팅을 클로징 한다.
     */
    void closingBetting(Power power);

    /**
     * 파워볼 게임을 롤백 한다.
     */
    void rollbackPayment(Power power);
}
