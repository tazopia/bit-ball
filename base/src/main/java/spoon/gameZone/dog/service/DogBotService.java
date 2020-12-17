package spoon.gameZone.dog.service;

import spoon.gameZone.dog.Dog;

import java.util.Date;

public interface DogBotService {

    boolean notExist(Date gameDate);

    void addGame(Dog dog);

    boolean closingGame(Dog result);

    void checkResult();

    void deleteGame(int days);

    String getLastGame(String edate);
}
