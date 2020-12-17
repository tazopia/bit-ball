package spoon.web.seller.sale;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.common.utils.WebUtils;
import spoon.member.domain.CurrentUser;
import spoon.payment.domain.MoneyDto;
import spoon.payment.service.PaymentListService;

@RequiredArgsConstructor
@Controller("seller.moneyController")
@RequestMapping("#{config.pathSeller}")
public class MoneyController {

    private final PaymentListService paymentListService;

    @RequestMapping(value = "sale/money", method = RequestMethod.GET)
    public String banking(ModelMap map, MoneyDto.SellerCommand command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "id") Pageable pageable) {
        CurrentUser user = WebUtils.user();
        command.setRole(user.getRole().name());
        command.setAgency(user.getUserid());

        map.addAttribute("page", paymentListService.sellerMoneyPage(command, pageable));
        map.addAttribute("command", command);

        return "seller/sale/money";
    }

}
