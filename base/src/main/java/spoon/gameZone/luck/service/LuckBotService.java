package spoon.gameZone.luck.service;

import spoon.gameZone.luck.Luck;

import java.util.Date;

public interface LuckBotService {

    boolean notExist(Date gameDate);

    void addGame(Luck luck);

    boolean closingGame(Luck result);

    void checkResult();

    void deleteGame(int days);

}
