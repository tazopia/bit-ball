package spoon.gameZone.crownBaccarat.service;

import spoon.gameZone.crownBaccarat.CrownBaccarat;

import java.util.Date;

public interface CrownBaccaratBotService {

    boolean notExist(Date gameDate);

    void addGame(CrownBaccarat crownBaccarat);

    boolean closingGame(CrownBaccarat result);

    void checkResult();

    void deleteGame(int days);

}
