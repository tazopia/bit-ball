package spoon.gameZone.soccer.service;

import spoon.gameZone.soccer.Soccer;

import java.util.Date;

public interface SoccerBotService {

    boolean notExist(Date gameDate);

    void addGame(Soccer soccer);

    boolean closingGame(Soccer result);

    void checkResult();

    void deleteGame(int days);

    String getLastGame(String edate);
}
