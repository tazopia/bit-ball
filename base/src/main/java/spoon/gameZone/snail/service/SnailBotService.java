package spoon.gameZone.snail.service;

import spoon.gameZone.snail.Snail;

import java.util.Date;

public interface SnailBotService {

    boolean notExist(Date gameDate);

    void addGame(Snail snail);

    boolean closingGame(Snail result);

    void checkResult();

    void deleteGame(int days);

}
