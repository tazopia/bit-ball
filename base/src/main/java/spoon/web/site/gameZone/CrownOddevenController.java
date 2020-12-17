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
import spoon.gameZone.crownOddeven.CrownOddevenDto;
import spoon.gameZone.crownOddeven.service.CrownOddevenGameService;
import spoon.gameZone.crownOddeven.service.CrownOddevenService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class CrownOddevenController {

    private CrownOddevenService crownOddevenService;

    private CrownOddevenGameService crownOddevenGameService;

    @RequestMapping(value = "zone/cw_oddeven", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(crownOddevenService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/cw_oddeven";
    }

    @RequestMapping(value = "zone/cw_oddeven/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", crownOddevenService.getClosing(command, pageable));
        return "site/score/cw_oddeven";
    }

    @ResponseBody
    @RequestMapping(value = "zone/cw_oddeven/config", method = RequestMethod.POST)
    public CrownOddevenDto.Config config() {
        return crownOddevenService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/cw_oddeven/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return crownOddevenGameService.betting(bet);
    }
}
