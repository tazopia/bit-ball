package spoon.web.site.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.banking.domain.BankingCode;
import spoon.banking.domain.WithdrawDto;
import spoon.banking.service.BankingListService;
import spoon.banking.service.BankingWithdrawService;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.WebUtils;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class WithdrawController {

    private BankingListService bankingListService;

    private BankingWithdrawService bankWithdrawService;

    private MemberService memberService;

    /**
     * 환전 신청 폼
     */
    @RequestMapping(value = "/payment/withdraw", method = RequestMethod.GET)
    public String withdraw(ModelMap map) {
        map.addAttribute("list", bankingListService.list(WebUtils.userid(), BankingCode.OUT));
        map.addAttribute("withdraw", new WithdrawDto.Add());
        map.addAttribute("zoneAmount", JsonUtils.toString(new WithdrawDto.Amount()));
        map.addAttribute("betting", bankWithdrawService.getRolling(WebUtils.userid()));
        map.addAttribute("member", memberService.getMember(WebUtils.userid()));

        return "/site/payment/withdraw";
    }

    /**
     * 환전 신청
     */
    @ResponseBody
    @RequestMapping(value = "/payment/withdraw", method = RequestMethod.POST)
    public AjaxResult withdraw(WithdrawDto.Add add) {
        return bankWithdrawService.withdraw(add);
    }


    /**
     * 환전 신청 삭제
     */
    @ResponseBody
    @RequestMapping(value = "/payment/withdraw/delete", method = RequestMethod.POST)
    public AjaxResult delete(long id) {
        return bankWithdrawService.delete(id);
    }

}
