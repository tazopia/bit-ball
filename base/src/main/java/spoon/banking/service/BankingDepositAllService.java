package spoon.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spoon.support.web.AjaxResult;

@RequiredArgsConstructor
@Service
public class BankingDepositAllService {

    private final BankingDepositService bankingDepositService;

    public AjaxResult submitAll(long[] ids) {
        int success = 0;
        int error = 0;
        AjaxResult result;

        for (long id : ids) {
            result = bankingDepositService.submit(id);
            if (result.isSuccess()) {
                success++;
            } else {
                error++;
            }
        }

        if (error == 0) {
            return new AjaxResult(true, "충전신청을 완료 하였습니다.");
        }

        return new AjaxResult(false, "충전신청 완료 : " + success + "건, 실패 : " + error + "건을 처리 하였습니다.");
    }

    public AjaxResult cancelAll(long[] ids) {
        int success = 0;
        int error = 0;
        AjaxResult result;

        for (long id : ids) {
            result = bankingDepositService.cancel(id);
            if (result.isSuccess()) {
                success++;
            } else {
                error++;
            }
        }

        if (error == 0) {
            return new AjaxResult(true, "충전신청을 취소하였습니다.");
        }

        return new AjaxResult(false, "충전신청 취소 : " + success + "건, 실패 : " + error + "건을 처리 하였습니다.");
    }

    public AjaxResult stopAll(long[] ids) {
        int success = 0;
        int error = 0;
        AjaxResult result;

        for (long id : ids) {
            result = bankingDepositService.stop(id);
            if (result.isSuccess()) {
                success++;
            } else {
                error++;
            }
        }

        if (error == 0) {
            return new AjaxResult(true);
        }

        return new AjaxResult(false, "충전신청 대기 : " + success + "건, 실패 : " + error + "건을 처리 하였습니다.");
    }
}
