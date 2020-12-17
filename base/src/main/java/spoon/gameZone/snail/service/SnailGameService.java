package spoon.gameZone.snail.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.snail.Snail;
import spoon.support.web.AjaxResult;

public interface SnailGameService {

    /**
     * 달팽이 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 달팽이 게임 베팅을 클로징 한다.
     */
    void closingBetting(Snail snail);

    /**
     * 달팽이 게임을 롤백 한다.
     */
    void rollbackPayment(Snail snail);
}
