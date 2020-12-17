package spoon.banking.service;

import spoon.banking.entity.Banking;
import spoon.support.web.AjaxResult;

public interface BankingService {

    /**
     * 아이디로 뱅킹 한개를 가져온다.
     */
    Banking getBanking(long id);

    /**
     * 대기중인 입출금 내역이 존재한다.
     */
    boolean existWorkBanking(String userid);

    /**
     * 입금 신청을 저장한다.
     */
    boolean addDeposit(Banking banking);

    /**
     * 입금 신청을 취소한다.
     */
    boolean cancelDeposit(long id);

    /**
     * 입금 신청을 완료한다.
     */
    boolean submitDeposit(long id);

    /**
     * 완료된 입금 신청을 롤백 시킨다.
     */
    AjaxResult rollbackDeposit(long id);

    /**
     * 시간 내에 환전신청이 있는지 확인한다.
     */
    boolean existBeforeMinutesWithdraw(String userid, int minutes);


    /**
     * 출금 신청을 저장한다.
     */
    boolean addWithdraw(Banking banking);

    /**
     * 총판 출금 신청을 저장한다.
     */
    boolean addSellerWithdraw(Banking banking);

    /**
     * 출금 신청을 완료 한다.
     */
    boolean submitWithdraw(Banking banking);

    /**
     * 출금 신청을 취소하고 사용자 머니를 롤백한다.
     */
    AjaxResult rollbackWithdraw(long id);

    /**
     * 입출금 내역을 삭제한다.
     */
    boolean delete(long id);


    /**
     * 입출금 내역 알람을 정지 시킨다.
     */
    void stop(long id);

    /**
     * 오늘날짜 첫충전인지 확인한다.
     */
    boolean isFirstDeposit(String userid);

    /**
     * 오늘날짜 매충전인지 확인한다.
     */
    boolean isEveryDeposit(String userid);

    /**
     * 오늘날짜 충전 금액을 가져온다.
     */
    long getTodayDeposit(String userid);

}
