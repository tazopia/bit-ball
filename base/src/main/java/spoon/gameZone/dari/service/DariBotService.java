package spoon.gameZone.dari.service;

import spoon.gameZone.dari.Dari;

import java.util.Date;

public interface DariBotService {

    boolean notExist(Date gameDate);

    void addGame(Dari dari);

    boolean closingGame(Dari result);

    void checkResult();

    void deleteGame(int days);

}
