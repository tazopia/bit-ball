package spoon.gameZone.newSnail.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.newSnail.NewSnail;
import spoon.support.web.AjaxResult;

public interface NewSnailGameService {

    /**
     * NEW 달팽이 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * NEW 달팽이 게임 베팅을 클로징 한다.
     */
    void closingBetting(NewSnail newSnail);

    /**
     * NEW 달팽이 게임을 롤백 한다.
     */
    void rollbackPayment(NewSnail newSnail);
}
