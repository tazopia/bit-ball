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
import spoon.config.domain.Config;
import spoon.inPlay.config.domain.InPlayLeagueDto;
import spoon.inPlay.config.service.InPlayLeagueService;
import spoon.inPlay.config.service.InPlaySportsService;


@RequiredArgsConstructor
@Controller(value = "admin.InPlayLeagueController")
@RequestMapping("#{config.pathAdmin}")
public class InPlayLeagueController {

    private final InPlaySportsService inPlaySportsService;

    private final InPlayLeagueService inPlayLeagueService;

    @GetMapping(value = "/inplay/league")
    public String list(Model model, InPlayLeagueDto.Command command,
                       @PageableDefault(size = 50, direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("sports", inPlaySportsService.getSports());
        model.addAttribute("page", inPlayLeagueService.getPage(pageable, command));
        model.addAttribute("command", command);

        return "admin/inplay/league/list";
    }

    /**
     * 관리자 > 환경설정 > 리그설정 > 수정 팝업
     */
    @GetMapping("/inplay/league/update")
    public String update(Model model, @RequestParam("name") String name, @RequestParam(value = "popup", defaultValue = "") String popup) {
        model.addAttribute("league", inPlayLeagueService.findOne(name));
        model.addAttribute("popup", popup);
        return "admin/inplay/league/update";
    }

    /**
     * 관리자 > 환경설정 > 리그설정 > 수정 팝업 프로세스
     */
    @PostMapping("/inplay/league/update")
    public String update(InPlayLeagueDto.Update update) {
        inPlayLeagueService.update(update);
        return "redirect:" + Config.getPathAdmin() + "/inplay/league/update?name=" + update.getName() + "&popup=closing";
    }
}
