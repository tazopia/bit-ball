package spoon.web.admin.accounting;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.accounting.domain.AccountingDto;
import spoon.accounting.service.AccountingService;
import spoon.common.utils.JsonUtils;
import spoon.member.service.MemberService;

@AllArgsConstructor
@Controller("admin.accountingController")
@RequestMapping("#{config.pathAdmin}")
public class AccountingController {

    private AccountingService accountingService;

    private MemberService memberService;

    @RequestMapping(value = "accounting/daily", method = RequestMethod.GET)
    public String daily(ModelMap map, @ModelAttribute("command") AccountingDto.Command command) {
        map.addAttribute("list", accountingService.daily(command));
        map.addAttribute("agencies", JsonUtils.toString(memberService.getAgencyList()));
        return "admin/accounting/daily";
    }

    @RequestMapping(value = "accounting/detail", method = RequestMethod.GET)
    public String detail(ModelMap map, @ModelAttribute("command") AccountingDto.Command command) {
        map.addAttribute("list", accountingService.gameAccount(command));
        map.addAttribute("amount", accountingService.amount());
        map.addAttribute("money", accountingService.money(command));
        map.addAttribute("point", accountingService.point(command));
        map.addAttribute("board", accountingService.board(command));
        map.addAttribute("comment", accountingService.comment(command));
        map.addAttribute("fees", accountingService.fees(command));
        map.addAttribute("agencies", JsonUtils.toString(memberService.getAgencyList()));

        return "admin/accounting/detail";
    }
}
