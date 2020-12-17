package spoon.web.seller.accounting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spoon.common.utils.WebUtils;
import spoon.member.domain.CurrentUser;
import spoon.sale.domain.SaleDto;
import spoon.share.service.ShareMoneyService;

@Slf4j
@RequiredArgsConstructor
@Controller("seller.shareMoneyController")
@RequestMapping("#{config.pathSeller}")
public class ShareMoneyController {

    private final ShareMoneyService shareMoneyService;

    @GetMapping("accounting/sale")
    public String shareSale(Model model, SaleDto.SaleCommand command,
                            @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = {"regDate"}) Pageable pageable) {
        CurrentUser user = WebUtils.user();
        command.setRole(user.getRole().name());
        command.setAgency(user.getUserid());

        model.addAttribute("page", shareMoneyService.page(command, pageable));

        return "/seller/accounting/sale";
    }

}
