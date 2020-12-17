package spoon.gameZone.newSnail.service;

import spoon.gameZone.newSnail.NewSnail;

import java.util.Date;

public interface NewSnailBotService {

    boolean notExist(Date gameDate);

    void addGame(NewSnail newSnail);

    boolean closingGame(NewSnail result);

    void checkResult();

    void deleteGame(int days);

}
