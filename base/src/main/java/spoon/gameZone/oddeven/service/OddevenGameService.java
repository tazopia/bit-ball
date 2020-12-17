package spoon.gameZone.oddeven.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.oddeven.Oddeven;
import spoon.support.web.AjaxResult;

public interface OddevenGameService {

    /**
     * 홀짝 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 홀짝 게임 베팅을 클로징 한다.
     */
    void closingBetting(Oddeven oddeven);

    /**
     * 홀짝 게임을 롤백 한다.
     */
    void rollbackPayment(Oddeven oddeven);
}
