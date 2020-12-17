package spoon.web.seller.accounting;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.common.utils.WebUtils;
import spoon.member.domain.CurrentUser;
import spoon.member.domain.Role;
import spoon.member.service.MemberService;
import spoon.sale.domain.SaleDto;
import spoon.sale.service.SaleService;

@Slf4j
@AllArgsConstructor
@Controller("seller.accountingController")
@RequestMapping("#{config.pathSeller}")
public class AccountingController {

    private SaleService saleService;

    private MemberService memberService;

    /**
     * 현재 정산금 페이지
     */
    @RequestMapping(value = "/accounting/current", method = RequestMethod.GET)
    public String sale(ModelMap map, SaleDto.AgencyCommand command) {
        CurrentUser user = WebUtils.user();
        command.setRole(user.getRole().name());
        command.setAgency(user.getUserid());

        map.addAttribute("list", saleService.currentAgencySale(command));

        if (user.getRole() == Role.AGENCY4)
            return "/seller/accounting/current4";
        if (user.getRole() == Role.AGENCY3)
            return "/seller/accounting/current3";
        if (user.getRole() == Role.AGENCY2)
            return "/seller/accounting/current2";
        return "/seller/accounting/current1";
    }

    /**
     * 총판 정산 리스트
     */
    @RequestMapping(value = "/accounting/list", method = RequestMethod.GET)
    public String list(ModelMap map, SaleDto.Command command, @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = {"regDate"}) Pageable pageable) {
        CurrentUser user = WebUtils.user();
        command.setAgency2(user.getAgency2());
        command.setAgency1(user.getAgency1());
        command.setAgency3(user.getAgency3());
        command.setAgency4(user.getAgency4());

        if (user.getRole() == Role.AGENCY2 || user.getRole() == Role.AGENCY3 || user.getRole() == Role.AGENCY4) {
            map.addAttribute("page", saleService.getPage(command, pageable));

            if (user.getRole() == Role.AGENCY4)
                return "/seller/accounting/list4";
            if (user.getRole() == Role.AGENCY3)
                return "/seller/accounting/list3";
            if (user.getRole() == Role.AGENCY2)
                return "/seller/accounting/list2";
        }

        map.addAttribute("page", saleService.getPageItem(command, pageable));
        return "/seller/accounting/item";
    }

}
