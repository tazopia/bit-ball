package spoon.inPlay.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spoon.common.utils.JsonUtils;
import spoon.inPlay.bet.service.InPlayBetGameService;
import spoon.inPlay.odds.service.InPlayGameService;

@RequiredArgsConstructor
@Controller(value = "admin.InPlayListController")
@RequestMapping("#{config.pathAdmin}")
public class InPlayListController {

    private final InPlayGameService inPlayGameService;

    private final InPlayBetGameService inPlayBetService;

    @GetMapping("/inplay")
    public String list(Model model) {
        model.addAttribute("list", inPlayGameService.adminList());
        return "admin/inplay/game/list";
    }


    @GetMapping("/inplay/odds")
    public String odds(Model model) {
        model.addAttribute("config", JsonUtils.toString(inPlayBetService.getInPlayConfig()));
        return "admin/inplay/game/odds";
    }
}
