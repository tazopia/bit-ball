package spoon.gameZone.KenoLadder.service;

import spoon.gameZone.KenoLadder.KenoLadder;

import java.util.Date;

public interface KenoLadderBotService {

    boolean notExist(Date gameDate);

    void addGame(KenoLadder ladder);

    boolean closingGame(KenoLadder result);

    void checkResult();

    void deleteGame(int days);

}
