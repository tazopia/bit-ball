package spoon.web.admin.payment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.member.service.MemberService;
import spoon.payment.domain.PaymentDto;
import spoon.payment.service.PaymentListService;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.moneyController")
@RequestMapping("#{config.pathAdmin}")
public class MoneyController {

    private PaymentListService paymentListService;

    private PaymentService paymentService;

    private MemberService memberService;

    /**
     * 전체 머니 변동 내역
     */
    @RequestMapping(value = "payment/money", method = RequestMethod.GET)
    public String moneyPage(ModelMap map, PaymentDto.Command command,
                            @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        map.addAttribute("page", paymentListService.moneyPage(command, pageable));
        return "admin/payment/money";
    }

    /**
     * 회원별 머니 변동 내역
     */
    @RequestMapping(value = "payment/money/{userid}", method = RequestMethod.GET)
    public String moneyPopup(ModelMap map, @PathVariable("userid") String userid, PaymentDto.Command command,
                             @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        command.setUserid(userid);
        map.addAttribute("page", paymentListService.moneyPage(command, pageable));
        map.addAttribute("member", memberService.getUser(userid));
        map.addAttribute("add", new PaymentDto.Add());
        return "admin/payment/popup/money";
    }

    /**
     * 회원별 입출금
     */
    @ResponseBody
    @RequestMapping(value = "payment/money/add", method = RequestMethod.POST)
    public AjaxResult moneyAdd(PaymentDto.Add add) {
        return paymentService.addMoney(add);
    }
}
