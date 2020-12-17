package spoon.gameZone.soccer.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.soccer.Soccer;
import spoon.support.web.AjaxResult;

public interface SoccerGameService {

    /**
     * 가상축구 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 가상축구 게임 베팅을 클로징 한다.
     */
    void closingBetting(Soccer soccer);

    /**
     * 가상축구 게임을 롤백 한다.
     */
    void rollbackPayment(Soccer soccer);

}
