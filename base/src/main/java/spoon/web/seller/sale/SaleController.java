package spoon.web.seller.sale;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.accounting.domain.AccountingDto;
import spoon.common.utils.WebUtils;
import spoon.member.domain.CurrentUser;
import spoon.member.service.MemberService;
import spoon.sale.service.AgencyService;

@Slf4j
@RequiredArgsConstructor
@Controller("seller.saleController")
@RequestMapping("#{config.pathSeller}")
public class SaleController {

    private final AgencyService agencyService;

    private final MemberService memberService;

    @RequestMapping(value = "sale/daily", method = RequestMethod.GET)
    public String daily(ModelMap map, @ModelAttribute("command") AccountingDto.Agency command) {
        CurrentUser user = WebUtils.user();
        command.setAgency(user.getUserid());
        command.setRole(user.getRole().name());

        map.addAttribute("list", agencyService.daily(command));
        return "seller/sale/daily";
    }

    @RequestMapping(value = "sale/detail", method = RequestMethod.GET)
    public String detail(ModelMap map, @ModelAttribute("command") AccountingDto.Agency command) {
        CurrentUser user = WebUtils.user();
        command.setAgency(user.getUserid());
        command.setRole(user.getRole().name());

        map.addAttribute("list", agencyService.gameAccount(command));
        map.addAttribute("amount", agencyService.amount(command));
        map.addAttribute("money", agencyService.money(command));
        map.addAttribute("point", agencyService.point(command));
        map.addAttribute("member", memberService.getMember(WebUtils.userid()));

        return "seller/sale/detail";
    }
}
