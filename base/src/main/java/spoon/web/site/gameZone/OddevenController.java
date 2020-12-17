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
import spoon.gameZone.oddeven.OddevenDto;
import spoon.gameZone.oddeven.service.OddevenGameService;
import spoon.gameZone.oddeven.service.OddevenService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class OddevenController {

    private OddevenService oddevenService;

    private OddevenGameService oddevenGameService;

    @RequestMapping(value = "zone/oddeven", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(oddevenService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/oddeven";
    }

    @RequestMapping(value = "zone/oddeven/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", oddevenService.getClosing(command, pageable));
        return "site/score/oddeven";
    }

    @ResponseBody
    @RequestMapping(value = "zone/oddeven/config", method = RequestMethod.POST)
    public OddevenDto.Config config() {
        return oddevenService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/oddeven/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return oddevenGameService.betting(bet);
    }
}
