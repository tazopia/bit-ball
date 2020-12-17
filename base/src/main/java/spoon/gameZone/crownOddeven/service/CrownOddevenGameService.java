package spoon.gameZone.crownOddeven.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.crownOddeven.CrownOddeven;
import spoon.support.web.AjaxResult;

public interface CrownOddevenGameService {

    /**
     * 홀짝 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 홀짝 게임 베팅을 클로징 한다.
     */
    void closingBetting(CrownOddeven crownOddeven);

    /**
     * 홀짝 게임을 롤백 한다.
     */
    void rollbackPayment(CrownOddeven crownOddeven);
}
