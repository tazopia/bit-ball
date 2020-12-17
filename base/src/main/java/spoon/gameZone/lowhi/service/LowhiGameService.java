package spoon.gameZone.lowhi.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.lowhi.Lowhi;
import spoon.support.web.AjaxResult;

public interface LowhiGameService {

    /**
     * 로하이 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 로하이 게임 베팅을 클로징 한다.
     */
    void closingBetting(Lowhi lowhi);

    /**
     * 알라딘 게임을 롤백 한다.
     */
    void rollbackPayment(Lowhi lowhi);
}
