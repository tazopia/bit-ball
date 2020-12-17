package spoon.inPlay.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.inPlay.config.domain.InPlayTeamDto;
import spoon.inPlay.config.service.InPlaySportsService;
import spoon.inPlay.config.service.InPlayTeamService;

@RequiredArgsConstructor
@Controller(value = "admin.InPlayTeamController")
@RequestMapping("#{config.pathAdmin}")
public class InPlayTeamController {

    private final InPlayTeamService inPlayTeamService;

    private final InPlaySportsService inPlaySportsService;

    @GetMapping("/inplay/team")
    public String list(Model model, InPlayTeamDto.Command command,
                       @PageableDefault(size = 50, direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("sports", inPlaySportsService.getSports());
        model.addAttribute("page", inPlayTeamService.getPage(pageable, command));
        model.addAttribute("command", command);

        return "admin/inplay/team/list";
    }

    @PostMapping("/inplay/team/update")
    public String update(InPlayTeamDto.Update update, RedirectAttributes ra) {
        inPlayTeamService.update(update);
        ra.addFlashAttribute("message", "팀명을 업데이트 하였습니다.");
        return "redirect:" + Config.getPathAdmin() + "/inplay/team";
    }

    @GetMapping("/inplay/team/popup")
    public String popupList(Model model, @RequestParam("name") String name, @RequestParam(value = "popup", defaultValue = "") String popup) {
        model.addAttribute("team", inPlayTeamService.findOne(name));
        model.addAttribute("popup", popup);
        return "admin/inplay/team/update";
    }

    @PostMapping("/inplay/team/popup")
    public String popupUpdate(InPlayTeamDto.Update update) {
        inPlayTeamService.update(update);
        return "redirect:" + Config.getPathAdmin() + "/inplay/team/popup?name=" + update.getName() + "&popup=closing";
    }

}
