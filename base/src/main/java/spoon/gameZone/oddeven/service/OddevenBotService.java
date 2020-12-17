package spoon.gameZone.oddeven.service;

import spoon.gameZone.oddeven.Oddeven;

import java.util.Date;

public interface OddevenBotService {

    boolean notExist(Date gameDate);

    void addGame(Oddeven oddeven);

    boolean closingGame(Oddeven result);

    void checkResult();

    void deleteGame(int days);

}
