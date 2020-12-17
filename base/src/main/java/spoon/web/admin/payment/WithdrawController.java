package spoon.web.admin.payment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.banking.domain.BankingCode;
import spoon.banking.domain.BankingDto;
import spoon.banking.service.BankingListService;
import spoon.banking.service.BankingWithdrawService;
import spoon.config.domain.Config;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.withdrawController")
@RequestMapping("#{config.pathAdmin}")
public class WithdrawController {

    private BankingListService bankingListService;

    private BankingWithdrawService bankingWithdrawService;

    /**
     * 환전 대기 페이지
     */
    @RequestMapping(value = "payment/withdraw", method = RequestMethod.GET)
    public String withdraw(ModelMap map, BankingDto.Command command,
                           @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        command.setClosing(false);
        command.setBankingCode(BankingCode.OUT);
        map.addAttribute("page", bankingListService.page(command, pageable));
        map.addAttribute("action", Config.getPathAdmin() + "/payment/withdraw");

        return "admin/payment/withdraw";
    }

    /**
     * 환전 완료 페이지
     */
    @RequestMapping(value = "payment/withdraw/closing", method = RequestMethod.GET)
    public String withdrawClosing(ModelMap map, BankingDto.Command command,
                                  @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        command.setClosing(true);
        command.setBankingCode(BankingCode.OUT);
        map.addAttribute("page", bankingListService.page(command, pageable));
        map.addAttribute("action", Config.getPathAdmin() + "/payment/withdraw/closing");

        return "admin/payment/withdraw";
    }


    /**
     * 환전 신청 완료 처리
     */
    @ResponseBody
    @RequestMapping(value = "payment/withdraw/submit", method = RequestMethod.POST)
    public AjaxResult submit(long id, long fees) {
        return bankingWithdrawService.submit(id, fees);
    }

    /**
     * 환전 신청 취소 처리
     */
    @ResponseBody
    @RequestMapping(value = "payment/withdraw/rollback", method = RequestMethod.POST)
    public AjaxResult cancel(long id) {
        return bankingWithdrawService.rollback(id);
    }

    /**
     * 환전 신청 알람 해제 처리
     */
    @ResponseBody
    @RequestMapping(value = "payment/withdraw/stop", method = RequestMethod.POST)
    public AjaxResult stop(long id) {
        return bankingWithdrawService.stop(id);
    }


}
