package spoon.web.seller.sale;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.banking.domain.BankingDto;
import spoon.banking.service.BankingListService;
import spoon.common.utils.WebUtils;
import spoon.member.domain.CurrentUser;

@AllArgsConstructor
@Controller("seller.bankController")
@RequestMapping("#{config.pathSeller}")
public class BankController {

    private BankingListService bankingListService;

    @RequestMapping(value = "sale/banking", method = RequestMethod.GET)
    public String banking(ModelMap map, BankingDto.Seller command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        CurrentUser user = WebUtils.user();
        command.setRole(user.getRole().name());
        command.setAgency(user.getUserid());

        map.addAttribute("page", bankingListService.bankingPage(command, pageable));
        map.addAttribute("command", command);

        return "seller/sale/banking";
    }

}
