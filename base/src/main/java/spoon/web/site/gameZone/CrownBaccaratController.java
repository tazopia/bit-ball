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
import spoon.gameZone.crownBaccarat.CrownBaccaratDto;
import spoon.gameZone.crownBaccarat.service.CrownBaccaratGameService;
import spoon.gameZone.crownBaccarat.service.CrownBaccaratService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class CrownBaccaratController {

    private CrownBaccaratService crownBaccaratService;

    private CrownBaccaratGameService crownBaccaratGameService;

    @RequestMapping(value = "zone/cw_baccarat", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(crownBaccaratService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/cw_baccarat";
    }

    @RequestMapping(value = "zone/cw_baccarat/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", crownBaccaratService.getClosing(command, pageable));
        return "site/score/cw_baccarat";
    }

    @ResponseBody
    @RequestMapping(value = "zone/cw_baccarat/config", method = RequestMethod.POST)
    public CrownBaccaratDto.Config config() {
        return crownBaccaratService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/cw_baccarat/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return crownBaccaratGameService.betting(bet);
    }
}
