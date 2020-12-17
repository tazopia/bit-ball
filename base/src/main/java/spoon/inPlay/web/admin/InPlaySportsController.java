package spoon.inPlay.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spoon.config.domain.Config;
import spoon.inPlay.config.domain.InPlaySportsDto;
import spoon.inPlay.config.service.InPlaySportsService;

@RequiredArgsConstructor
@Controller(value = "admin.InPlaySportsController")
@RequestMapping("#{config.pathAdmin}")
public class InPlaySportsController {

    private final InPlaySportsService inPlaySportsService;

    @GetMapping("inplay/sports")
    public String list(Model model) {
        model.addAttribute("list", inPlaySportsService.getSports());
        return "admin/inplay/sports/list";
    }

    @GetMapping("inplay/sports/update")
    public String updatePopup(ModelMap map, @RequestParam("name") String name, @RequestParam(value = "popup", defaultValue = "") String popup) {
        map.addAttribute("sports", inPlaySportsService.findOne(name));
        map.addAttribute("popup", popup);
        return "admin/inplay/sports/update";
    }

    /**
     * 관리자 > 환경설정 > 종목설정 > 수정 팝업 프로세스
     */
    @PostMapping("inplay/sports/update")
    public String updatePopup(InPlaySportsDto.Update update) {
        inPlaySportsService.update(update);
        return "redirect:" + Config.getPathAdmin() + "/inplay/sports/update?name=" + update.getName() + "&popup=closing";
    }


}
