package spoon.web.admin.bet;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.bot.balance.service.BalanceService;

@AllArgsConstructor
@Controller("admin.balanceController")
@RequestMapping("#{config.pathAdmin}")
public class BalanceController {

    private BalanceService balanceService;

    @RequestMapping(value = "betting/balance", method = RequestMethod.GET)
    public String polygon(ModelMap map,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        map.addAttribute("page", balanceService.pagePolygon(pageable));

        return "admin/betting/polygon";
    }

}
