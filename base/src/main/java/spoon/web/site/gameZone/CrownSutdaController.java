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
import spoon.gameZone.crownSutda.CrownSutdaDto;
import spoon.gameZone.crownSutda.service.CrownSutdaGameService;
import spoon.gameZone.crownSutda.service.CrownSutdaService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class CrownSutdaController {

    private CrownSutdaService crownSutdaService;

    private CrownSutdaGameService crownSutdaGameService;

    @RequestMapping(value = "zone/cw_sutda", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(crownSutdaService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/cw_sutda";
    }

    @RequestMapping(value = "zone/cw_sutda/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", crownSutdaService.getClosing(command, pageable));
        return "site/score/cw_sutda";
    }

    @ResponseBody
    @RequestMapping(value = "zone/cw_sutda/config", method = RequestMethod.POST)
    public CrownSutdaDto.Config config() {
        return crownSutdaService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/cw_sutda/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return crownSutdaGameService.betting(bet);
    }
}
