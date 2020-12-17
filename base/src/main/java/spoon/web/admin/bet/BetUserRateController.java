package spoon.web.admin.bet;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.bet.domain.BetDto;
import spoon.bet.domain.BetUserRate;
import spoon.bet.service.BetUserRateService;

import java.util.List;

@AllArgsConstructor
@Controller("admin.betUserRateController")
@RequestMapping("#{config.pathAdmin}")
public class BetUserRateController {

    private BetUserRateService betUserRateService;

    /**
     * 개인별 베팅현황
     */
    @RequestMapping(value = "/betting/user", method = RequestMethod.GET)
    public String betUserRate(ModelMap map, @ModelAttribute("command") BetDto.BetRate command,
                              @PageableDefault(size = 20) Pageable pageable) {
        command.setSize(pageable.getPageSize());
        List<BetUserRate> list = betUserRateService.getBetUserRate(command);
        Page<BetUserRate> page = new PageImpl<>(list, pageable, betUserRateService.getBetUserRateTotal(command));
        map.addAttribute("page", page);

        return "/admin/betting/user";
    }
}
