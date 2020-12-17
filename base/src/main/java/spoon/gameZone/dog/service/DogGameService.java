package spoon.gameZone.dog.service;

import spoon.gameZone.ZoneDto;
import spoon.gameZone.dog.Dog;
import spoon.support.web.AjaxResult;

public interface DogGameService {

    /**
     * 개경주 게임을 베팅한다.
     */
    AjaxResult betting(ZoneDto.Bet bet);

    /**
     * 개경주 게임 베팅을 클로징 한다.
     */
    void closingBetting(Dog dog);

    /**
     * 개경주 게임을 롤백 한다.
     */
    void rollbackPayment(Dog dog);

}
