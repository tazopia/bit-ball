package spoon.inPlay.web.site;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spoon.common.utils.JsonUtils;
import spoon.inPlay.bet.service.InPlayBetGameService;
import spoon.member.domain.UserBetInfo;

@RequiredArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class InPlayListController {

    private final InPlayBetGameService inPlayBetService;

    @GetMapping("/inplay")
    public String list(Model model) {
        model.addAttribute("config", JsonUtils.toString(inPlayBetService.getInPlayConfig()));
        model.addAttribute("betInfo", new UserBetInfo());
        return "site/inplay/list";
    }
}
