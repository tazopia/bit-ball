package spoon.gameZone.baccarat.service;

import spoon.gameZone.baccarat.Baccarat;

import java.util.Date;

public interface BaccaratBotService {

    boolean notExist(Date gameDate);

    void addGame(Baccarat baccarat);

    boolean closingGame(Baccarat result);

    void checkResult();

    void deleteGame(int days);

}
