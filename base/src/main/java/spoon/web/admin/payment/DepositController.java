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
import spoon.banking.service.BankingDepositService;
import spoon.banking.service.BankingListService;
import spoon.config.domain.Config;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.depositController")
@RequestMapping("#{config.pathAdmin}")
public class DepositController {

    private BankingListService bankingListService;

    private BankingDepositService bankingDepositService;

    /**
     * 입금 대기 페이지
     */
    @RequestMapping(value = "payment/deposit", method = RequestMethod.GET)
    public String deposit(ModelMap map, BankingDto.Command command,
                          @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        command.setClosing(false);
        command.setBankingCode(BankingCode.IN);
        map.addAttribute("page", bankingListService.page(command, pageable));
        map.addAttribute("action", Config.getPathAdmin() + "/payment/deposit");

        return "admin/payment/deposit";
    }

    /**
     * 입금 완료 페이지
     */
    @RequestMapping(value = "payment/deposit/closing", method = RequestMethod.GET)
    public String depositClosing(ModelMap map, BankingDto.Command command,
                                 @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        command.setClosing(true);
        command.setBankingCode(BankingCode.IN);
        map.addAttribute("page", bankingListService.page(command, pageable));
        map.addAttribute("action", Config.getPathAdmin() + "/payment/deposit/closing");

        return "admin/payment/deposit";
    }

    /**
     * 입금 신청 완료 처리
     */
    @ResponseBody
    @RequestMapping(value = "payment/deposit/submit", method = RequestMethod.POST)
    public AjaxResult submit(long id) {
        return bankingDepositService.submit(id);
    }

    /**
     * 입금 신청 취소 처리
     */
    @ResponseBody
    @RequestMapping(value = "payment/deposit/cancel", method = RequestMethod.POST)
    public AjaxResult cancel(long id) {
        return bankingDepositService.cancel(id);
    }

    /**
     * 입금 신청 알람 해제 처리
     */
    @ResponseBody
    @RequestMapping(value = "payment/deposit/stop", method = RequestMethod.POST)
    public AjaxResult stop(long id) {
        return bankingDepositService.stop(id);
    }

    /**
     * 입금 신청 완료 롤백
     */
    @ResponseBody
    @RequestMapping(value = "payment/deposit/rollback", method = RequestMethod.POST)
    public AjaxResult rollback(long id) {
        return bankingDepositService.rollback(id);
    }

}
