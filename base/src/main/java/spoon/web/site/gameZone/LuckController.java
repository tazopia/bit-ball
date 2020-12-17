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
import spoon.gameZone.luck.LuckDto;
import spoon.gameZone.luck.service.LuckGameService;
import spoon.gameZone.luck.service.LuckService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class LuckController {

    private LuckService luckService;

    private LuckGameService luckGameService;

    @RequestMapping(value = "zone/luck", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(luckService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/luck";
    }

    @RequestMapping(value = "zone/luck/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", luckService.getClosing(command, pageable));
        return "site/score/luck";
    }

    @ResponseBody
    @RequestMapping(value = "zone/luck/config", method = RequestMethod.POST)
    public LuckDto.Config config() {
        return luckService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/luck/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return luckGameService.betting(bet);
    }

}
