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
import spoon.gameZone.dari.DariDto;
import spoon.gameZone.dari.service.DariGameService;
import spoon.gameZone.dari.service.DariService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class DariController {

    private DariService dariService;

    private DariGameService dariGameService;

    @RequestMapping(value = "zone/dari", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(dariService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/dari";
    }

    @RequestMapping(value = "zone/dari/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", dariService.getClosing(command, pageable));
        return "site/score/dari";
    }

    @ResponseBody
    @RequestMapping(value = "zone/dari/config", method = RequestMethod.POST)
    public DariDto.Config config() {
        return dariService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/dari/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return dariGameService.betting(bet);
    }
}
