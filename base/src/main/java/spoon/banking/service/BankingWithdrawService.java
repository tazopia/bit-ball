package spoon.banking.service;

import spoon.banking.domain.Rolling;
import spoon.banking.domain.WithdrawDto;
import spoon.support.web.AjaxResult;

public interface BankingWithdrawService {

    /**
     * 환전 신청 하기
     */
    AjaxResult withdraw(WithdrawDto.Add add);

    /**
     * 총판 환전 신청 하기
     */
    AjaxResult sellerWithdraw(WithdrawDto.Add add);

    /**
     * 롤링을 구한다.
     */
    Rolling getRolling(String userid);

    /**
     * 환전 신청 완료
     */
    AjaxResult submit(long id, long fees);

    /**
     * 환전 신청 취소
     */
    AjaxResult rollback(long id);

    /**
     * 알람 해제
     */
    AjaxResult stop(long id);

    /**
     * 내역 삭제
     */
    AjaxResult delete(long id);

}
