package spoon.gameZone.crownSutda.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.crownSutda.CrownSutda;
import spoon.support.web.AjaxResult;

public interface CrownSutdaGameService {

    /**
     * 섰다 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 섰다 게임 베팅을 클로징 한다.
     */
    void closingBetting(CrownSutda crownSutda);

    /**
     * 섰다 게임을 롤백 한다.
     */
    void rollbackPayment(CrownSutda crownSutda);
}
