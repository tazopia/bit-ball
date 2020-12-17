package spoon.gameZone.ladder.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.ladder.Ladder;
import spoon.support.web.AjaxResult;

public interface LadderGameService {

    /**
     * 사다리 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 사다리 게임 베팅을 클로징 한다.
     */
    void closingBetting(Ladder ladder);

    /**
     * 사다리 게임을 롤백 한다.
     */
    void rollbackPayment(Ladder ladder);
}
