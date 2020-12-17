package spoon.gameZone.crownSutda.service;

import spoon.gameZone.crownSutda.CrownSutda;

import java.util.Date;

public interface CrownSutdaBotService {

    boolean notExist(Date gameDate);

    void addGame(CrownSutda crownSutda);

    boolean closingGame(CrownSutda result);

    void checkResult();

    void deleteGame(int days);

}
