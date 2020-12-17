package spoon.web.admin.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spoon.banking.service.BankingDepositAllService;
import spoon.support.web.AjaxResult;

@Slf4j
@RequiredArgsConstructor
@RestController("admin.depositRestController")
@RequestMapping("#{config.pathAdmin}")
public class DepositRestController {

    private final BankingDepositAllService bankingDepositAllService;

    /**
     * 입금 신청 완료 처리
     */
    @RequestMapping(value = "payment/deposit/submitAll", method = RequestMethod.POST)
    public AjaxResult submit(long[] ids) {
        return bankingDepositAllService.submitAll(ids);
    }

    /**
     * 입금 신청 취소 처리
     */
    @RequestMapping(value = "payment/deposit/cancelAll", method = RequestMethod.POST)
    public AjaxResult cancel(long[] ids) {
        return bankingDepositAllService.cancelAll(ids);
    }

    /**
     * 입금 신청 알람 해제 처리
     */
    @RequestMapping(value = "payment/deposit/stopAll", method = RequestMethod.POST)
    public AjaxResult stop(long[] ids) {
        return bankingDepositAllService.stopAll(ids);
    }
}
