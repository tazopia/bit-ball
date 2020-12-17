package spoon.web.site.gameZone;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import spoon.common.utils.JsonUtils;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.lowhi.LowhiDto;
import spoon.gameZone.lowhi.service.LowhiGameService;
import spoon.gameZone.lowhi.service.LowhiService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class LowhiController {

    private LowhiService lowhiService;

    private LowhiGameService lowhiGameService;

    @RequestMapping(value = "zone/lowhi", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(lowhiService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/lowhi";
    }

    @RequestMapping(value = "zone/lowhi/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", lowhiService.getClosing(command, pageable));
        return "site/score/lowhi";
    }

    @ResponseBody
    @RequestMapping(value = "zone/lowhi/config", method = RequestMethod.POST)
    public LowhiDto.Config config() {
        return lowhiService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/lowhi/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return lowhiGameService.betting(bet);
    }
}
