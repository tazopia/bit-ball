package spoon.gameZone.lowhi.service;

import spoon.gameZone.lowhi.Lowhi;

import java.util.Date;

public interface LowhiBotService {

    boolean notExist(Date gameDate);

    void addGame(Lowhi lowhi);

    boolean closingGame(Lowhi result);

    void checkResult();

    void deleteGame(int days);

}
