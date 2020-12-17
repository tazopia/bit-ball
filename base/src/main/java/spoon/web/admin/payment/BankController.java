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
import spoon.banking.domain.BankingDto;
import spoon.banking.service.BankingListService;
import spoon.member.service.MemberService;

@Slf4j
@AllArgsConstructor
@Controller("admin.bankController")
@RequestMapping("#{config.pathAdmin}")
public class BankController {

    private BankingListService bankingListService;

    private MemberService memberService;

    @RequestMapping(value = "payment/banking/{userid}", method = RequestMethod.GET)
    public String banking(ModelMap map, @PathVariable("userid") String userid, BankingDto.Date command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        command.setUserid(userid);
        map.addAttribute("command", command);
        map.addAttribute("money", bankingListService.bankingTotal(command));
        map.addAttribute("page", bankingListService.bankingPage(userid, pageable));
        map.addAttribute("member", memberService.getUser(userid));
        return "admin/payment/popup/banking";
    }

}
