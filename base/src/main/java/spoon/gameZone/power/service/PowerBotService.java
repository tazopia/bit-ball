package spoon.gameZone.power.service;

import spoon.gameZone.power.Power;

import java.util.Date;

public interface PowerBotService {

    boolean notExist(Date gameDate);

    void addGame(Power power);

    boolean closingGame(Power result);

    void checkResult();

    void deleteGame(int days);

}
