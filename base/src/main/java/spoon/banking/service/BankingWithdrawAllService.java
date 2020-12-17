package spoon.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spoon.support.web.AjaxResult;

@RequiredArgsConstructor
@Service
public class BankingWithdrawAllService {

    private final BankingWithdrawService bankingWithdrawService;


    public AjaxResult submitAll(long[] ids, long[] fees) {
        int success = 0;
        int error = 0;
        AjaxResult result;

        for (int i = 0; i < ids.length; i++) {
            result = bankingWithdrawService.submit(ids[i], fees[i]);
            if (result.isSuccess()) {
                success++;
            } else {
                error++;
            }
        }

        if (error == 0) {
            return new AjaxResult(true, "환전신청을 완료 하였습니다.");
        }

        return new AjaxResult(false, "환전신청 완료 : " + success + "건, 실패 : " + error + "건을 처리 하였습니다.");
    }

    public AjaxResult rollbackAll(long[] ids) {
        int success = 0;
        int error = 0;
        AjaxResult result;

        for (long id : ids) {
            result = bankingWithdrawService.rollback(id);
            if (result.isSuccess()) {
                success++;
            } else {
                error++;
            }
        }

        if (error == 0) {
            return new AjaxResult(true, "환전신청을 취소하였습니다.");
        }

        return new AjaxResult(false, "환전신청 취소 : " + success + "건, 실패 : " + error + "건을 처리 하였습니다.");
    }

    public AjaxResult stopAll(long[] ids) {
        int success = 0;
        int error = 0;
        AjaxResult result;

        for (long id : ids) {
            result = bankingWithdrawService.stop(id);
            if (result.isSuccess()) {
                success++;
            } else {
                error++;
            }
        }

        if (error == 0) {
            return new AjaxResult(true);
        }

        return new AjaxResult(false, "환전신청 대기 : " + success + "건, 실패 : " + error + "건을 처리 하였습니다.");
    }
}
