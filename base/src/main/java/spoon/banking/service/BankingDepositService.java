package spoon.banking.service;

import spoon.banking.domain.DepositDto;
import spoon.support.web.AjaxResult;

public interface BankingDepositService {

    /**
     * 충전 신청하기
     */
    AjaxResult deposit(DepositDto.Add add);

    /**
     * 충전내역 삭제 (사용자만 가능함)
     */
    AjaxResult delete(long id);

    /**
     * 충전신청 취소
     */
    AjaxResult cancel(long id);

    /**
     * 충전신청 - 입금확인 머니지급
     */
    AjaxResult submit(long id);

    /**
     * 충전신청 알람 대기
     */
    AjaxResult stop(long id);

    /**
     * 충전신청 롤백
     */
    AjaxResult rollback(long id);

}
