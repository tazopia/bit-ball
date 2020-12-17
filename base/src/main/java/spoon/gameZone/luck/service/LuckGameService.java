package spoon.gameZone.luck.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.luck.Luck;
import spoon.support.web.AjaxResult;

public interface LuckGameService {

    /**
     * 세븐럭 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 세븐럭 게임 베팅을 클로징 한다.
     */
    void closingBetting(Luck luck);

    /**
     * 세븐럭 게임을 롤백 한다.
     */
    void rollbackPayment(Luck luck);
}
