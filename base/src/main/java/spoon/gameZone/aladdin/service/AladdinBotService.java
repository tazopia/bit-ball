package spoon.gameZone.aladdin.service;

import spoon.gameZone.aladdin.Aladdin;

import java.util.Date;

public interface AladdinBotService {

    boolean notExist(Date gameDate);

    void addGame(Aladdin aladdin);

    boolean closingGame(Aladdin result);

    void checkResult();

    void deleteGame(int days);

}
