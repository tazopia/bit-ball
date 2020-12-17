package spoon.gameZone.powerLadder.service;

import spoon.gameZone.powerLadder.PowerLadder;

import java.util.Date;

public interface PowerLadderBotService {

    boolean notExist(Date gameDate);

    void addGame(PowerLadder powerLadder);

    boolean closingGame(PowerLadder result);

    void checkResult();

    void deleteGame(int days);

}
