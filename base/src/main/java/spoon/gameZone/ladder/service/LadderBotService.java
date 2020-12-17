package spoon.gameZone.ladder.service;

import spoon.gameZone.ladder.Ladder;

import java.util.Date;

public interface LadderBotService {

    boolean notExist(Date gameDate);

    void addGame(Ladder ladder);

    boolean closingGame(Ladder result);

    void checkResult();

    void deleteGame(int days);

}
