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
import spoon.gameZone.snail.SnailDto;
import spoon.gameZone.snail.service.SnailGameService;
import spoon.gameZone.snail.service.SnailService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class SnailController {

    private SnailService snailService;

    private SnailGameService snailGameService;

    @RequestMapping(value = "zone/snail", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(snailService.gameConfig()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/snail";
    }

    @RequestMapping(value = "zone/snail/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", snailService.getClosing(command, pageable));
        return "site/score/snail";
    }

    @ResponseBody
    @RequestMapping(value = "zone/snail/config", method = RequestMethod.POST)
    public SnailDto.Config config() {
        return snailService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/snail/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return snailGameService.betting(bet);
    }
}
