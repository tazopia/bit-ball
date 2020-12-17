package spoon.web.site.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.banking.service.ExchangeService;
import spoon.common.utils.WebUtils;
import spoon.payment.service.PaymentListService;
import spoon.support.web.AjaxResult;

@RequiredArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class PointController {

    private final ExchangeService exchangeService;

    private final PaymentListService paymentListService;

    /**
     * 충전 신청 폼
     */
    @RequestMapping(value = "/payment/point", method = RequestMethod.GET)
    public String point(ModelMap map,
                        @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        map.addAttribute("page", paymentListService.pointPage(WebUtils.userid(), pageable));
        return "/site/payment/point";
    }

    @ResponseBody
    @RequestMapping(value = "/payment/exchange", method = RequestMethod.POST)
    public AjaxResult exchange() {
        return exchangeService.exchange();
    }
}
