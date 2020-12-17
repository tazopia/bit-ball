package spoon.web.seller.sale;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.bet.domain.BetDto;
import spoon.bet.service.BetListService;
import spoon.common.utils.WebUtils;
import spoon.inPlay.bet.service.InPlayBetService;
import spoon.member.domain.CurrentUser;

@RequiredArgsConstructor
@Controller("seller.betController")
@RequestMapping("#{config.pathSeller}")
public class BetController {

    private final BetListService betListService;

    private final InPlayBetService inPlayBetService;

    @RequestMapping(value = "sale/betting", method = RequestMethod.GET)
    public String betting(ModelMap map, BetDto.SellerCommand command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "betDate") Pageable pageable) {
        CurrentUser user = WebUtils.user();
        command.setAgency(user.getUserid());
        command.setRole(user.getRole().name());

        map.addAttribute("page", betListService.sellerPage(command, pageable));
        map.addAttribute("command", command);
        return "seller/sale/betting";
    }

    @RequestMapping(value = "sale/inplay", method = RequestMethod.GET)
    public String inplay(ModelMap map, BetDto.SellerCommand command,
                         @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "betDate") Pageable pageable) {
        CurrentUser user = WebUtils.user();
        command.setAgency(user.getUserid());
        command.setRole(user.getRole().name());

        map.addAttribute("page", inPlayBetService.sellerPage(command, pageable));
        map.addAttribute("command", command);
        return "seller/sale/inplay";
    }
}
