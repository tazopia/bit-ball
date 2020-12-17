package spoon.web.admin.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spoon.banking.service.BankingWithdrawAllService;
import spoon.support.web.AjaxResult;

@Slf4j
@RequiredArgsConstructor
@RestController("admin.withdrawRestController")
@RequestMapping("#{config.pathAdmin}")
public class WithdrawRestController {

    private final BankingWithdrawAllService bankingWithdrawAllService;

    /**
     * 환전 신청 완료 처리
     */
    @RequestMapping(value = "payment/withdraw/submitAll", method = RequestMethod.POST)
    public AjaxResult submit(long[] ids, long[] fees) {
        return bankingWithdrawAllService.submitAll(ids, fees);
    }

    /**
     * 환전 신청 취소 처리
     */
    @RequestMapping(value = "payment/withdraw/rollbackAll", method = RequestMethod.POST)
    public AjaxResult cancel(long[] ids) {
        return bankingWithdrawAllService.rollbackAll(ids);
    }

    /**
     * 환전 신청 알람 해제 처리
     */
    @RequestMapping(value = "payment/withdraw/stopAll", method = RequestMethod.POST)
    public AjaxResult stop(long[] ids) {
        return bankingWithdrawAllService.stopAll(ids);
    }


}
