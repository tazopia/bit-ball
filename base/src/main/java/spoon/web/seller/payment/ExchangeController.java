package spoon.web.seller.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.common.utils.WebUtils;
import spoon.member.domain.MemberDto;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.payment.domain.PaymentDto;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller("seller.exchangeController")
@RequestMapping("#{config.pathSeller}")
public class ExchangeController {

    private MemberService memberService;

    private PaymentService paymentService;

    @RequestMapping(value = "/payment/exchange", method = RequestMethod.GET)
    public String exchange(ModelMap map) {
        Member member = memberService.getMember(WebUtils.userid());

        MemberDto.Seller command = new MemberDto.Seller();
        command.setAgency(member.getUserid());
        command.setRole(member.getRole().name());

        map.addAttribute("member", member);
        map.addAttribute("list", memberService.getExchangeList(command));

        return "seller/payment/exchange";
    }

    @ResponseBody
    @RequestMapping(value = "/payment/exchange", method = RequestMethod.POST)
    public AjaxResult exchange(PaymentDto.Add add, @RequestParam("bankPassword") String bankPassword) {
        return paymentService.exchange(add, bankPassword);
    }

}
