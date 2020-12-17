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
@Controller("admin.pointController")
@RequestMapping("#{config.pathAdmin}")
public class PointController {

    private PaymentListService paymentListService;

    private PaymentService paymentService;

    private MemberService memberService;

    /**
     * 전체 포인트 변동 내역
     */
    @RequestMapping(value = "payment/point", method = RequestMethod.GET)
    public String pointPage(ModelMap map, PaymentDto.Command command,
                            @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        map.addAttribute("page", paymentListService.pointPage(command, pageable));
        return "admin/payment/point";
    }

    /**
     * 회원별 포인트 변동 내역
     */
    @RequestMapping(value = "payment/point/{userid}", method = RequestMethod.GET)
    public String pointPopup(ModelMap map, @PathVariable("userid") String userid, PaymentDto.Command command,
                             @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        command.setUserid(userid);
        map.addAttribute("page", paymentListService.pointPage(command, pageable));
        map.addAttribute("member", memberService.getUser(userid));
        map.addAttribute("add", new PaymentDto.Add());
        return "admin/payment/popup/point";
    }

    /**
     * 회원별 포인트 추가/제거
     */
    @ResponseBody
    @RequestMapping(value = "payment/point/add", method = RequestMethod.POST)
    public AjaxResult pointAdd(PaymentDto.Add add) {
        return paymentService.addPoint(add);
    }

}
