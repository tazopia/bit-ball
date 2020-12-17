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
import spoon.gameZone.aladdin.AladdinDto;
import spoon.gameZone.aladdin.service.AladdinGameService;
import spoon.gameZone.aladdin.service.AladdinService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class AladdinController {

    private AladdinService aladdinService;

    private AladdinGameService aladdinGameService;

    @RequestMapping(value = "zone/aladdin", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(aladdinService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/aladdin";
    }

    @RequestMapping(value = "zone/aladdin/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", aladdinService.getClosing(command, pageable));
        return "site/score/aladdin";
    }

    @ResponseBody
    @RequestMapping(value = "zone/aladdin/config", method = RequestMethod.POST)
    public AladdinDto.Config config() {
        return aladdinService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/aladdin/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return aladdinGameService.betting(bet);
    }
}
