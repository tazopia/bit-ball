package spoon.gameZone.baccarat.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.baccarat.Baccarat;
import spoon.support.web.AjaxResult;

public interface BaccaratGameService {

    /**
     * 바카라 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 바카라 게임 베팅을 클로징 한다.
     */
    void closingBetting(Baccarat baccarat);

    /**
     * 바카라 게임을 롤백 한다.
     */
    void rollbackPayment(Baccarat baccarat);
}
