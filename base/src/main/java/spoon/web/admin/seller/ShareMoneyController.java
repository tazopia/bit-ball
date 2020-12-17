package spoon.web.admin.seller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spoon.common.utils.JsonUtils;
import spoon.member.service.MemberService;
import spoon.sale.domain.SaleDto;
import spoon.share.service.ShareMoneyService;

@Slf4j
@RequiredArgsConstructor
@Controller("admin.shareMoneyController")
@RequestMapping("#{config.pathAdmin}")
public class ShareMoneyController {

    private final ShareMoneyService shareMoneyService;

    private final MemberService memberService;

    @GetMapping("seller/rolling")
    public String shareSale(Model model, SaleDto.SaleCommand command,
                            @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = {"regDate"}) Pageable pageable) {
        model.addAttribute("agencies", JsonUtils.toString(memberService.getAgencyList()));
        model.addAttribute("command", command);
        model.addAttribute("page", shareMoneyService.page(command, pageable));

        return "/admin/seller/rolling";
    }

}
