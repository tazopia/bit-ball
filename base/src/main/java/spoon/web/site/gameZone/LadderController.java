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
import spoon.gameZone.ladder.LadderDto;
import spoon.gameZone.ladder.service.LadderGameService;
import spoon.gameZone.ladder.service.LadderService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class LadderController {

    private LadderService ladderService;

    private LadderGameService ladderGameService;

    @RequestMapping(value = "zone/ladder", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(ladderService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/ladder";
    }

    @RequestMapping(value = "zone/ladder/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", ladderService.getClosing(command, pageable));
        return "site/score/ladder";
    }

    @ResponseBody
    @RequestMapping(value = "zone/ladder/config", method = RequestMethod.POST)
    public LadderDto.Config config() {
        return ladderService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/ladder/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return ladderGameService.betting(bet);
    }

}
