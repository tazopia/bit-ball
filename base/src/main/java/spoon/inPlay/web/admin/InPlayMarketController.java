package spoon.inPlay.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.inPlay.config.domain.InPlayLeagueDto;
import spoon.inPlay.config.domain.InPlayMarketDto;
import spoon.inPlay.config.service.InPlayMarketService;
import spoon.support.web.AjaxResult;

@RequiredArgsConstructor
@Controller(value = "admin.InPlayMarketController")
@RequestMapping("#{config.pathAdmin}")
public class InPlayMarketController {

    private final InPlayMarketService inPlayMarketService;

    @GetMapping(value = "/inplay/market")
    public String list(Model model, InPlayMarketDto.Command command) {
        model.addAttribute("list", inPlayMarketService.getList(command));
        model.addAttribute("command", command);

        return "admin/inplay/market/list";
    }

    @GetMapping("/inplay/market/update")
    public String update(Model model, @RequestParam("id") Long id) {
        model.addAttribute("market", inPlayMarketService.findOne(id));
        return "admin/inplay/market/update";
    }

    @PostMapping("/inplay/market/update")
    public String update(InPlayMarketDto.Update update, RedirectAttributes ra) {
        inPlayMarketService.update(update);
        ra.addFlashAttribute("popup", "closing");
        return "redirect:" + Config.getPathAdmin() + "/inplay/market/update?id=" + update.getId();
    }

    @ResponseBody
    @PostMapping("/inplay/market/up")
    public AjaxResult up(Long id) {
        return inPlayMarketService.up(id);
    }

    @ResponseBody
    @PostMapping("/inplay/market/down")
    public AjaxResult down(Long id) {
        return inPlayMarketService.down(id);
    }

}
