package spoon.monitor.service;

import spoon.monitor.domain.Monitor;

public interface MonitorService {

    Monitor getMonitor();

    void initMonitor();

    void checkDeposit();

    void checkWithdraw();

    void checkQna();

    void checkBlack();

    void checkMember();

    void checkBoard();

    void checkSports();

    void checkAmount();
}
