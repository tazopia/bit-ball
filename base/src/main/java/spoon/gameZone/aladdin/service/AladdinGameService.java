package spoon.gameZone.aladdin.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.aladdin.Aladdin;
import spoon.support.web.AjaxResult;

public interface AladdinGameService {

    /**
     * 알라딘 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 알라딘 게임 베팅을 클로징 한다.
     */
    void closingBetting(Aladdin aladdin);

    /**
     * 알라딘 게임을 롤백 한다.
     */
    void rollbackPayment(Aladdin aladdin);
}
