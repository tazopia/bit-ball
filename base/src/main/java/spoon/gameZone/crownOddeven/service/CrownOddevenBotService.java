package spoon.gameZone.crownOddeven.service;

import spoon.gameZone.crownOddeven.CrownOddeven;

import java.util.Date;

public interface CrownOddevenBotService {

    boolean notExist(Date gameDate);

    void addGame(CrownOddeven crownOddeven);

    boolean closingGame(CrownOddeven result);

    void checkResult();

    void deleteGame(int days);

}
