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
import spoon.gameZone.soccer.SoccerDto;
import spoon.gameZone.soccer.service.SoccerGameService;
import spoon.gameZone.soccer.service.SoccerService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class SoccerController {

    private SoccerService soccerService;

    private SoccerGameService soccerGameService;

    @RequestMapping(value = "zone/soccer", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(soccerService.gameConfig()));
        map.addAttribute("list", JsonUtils.toString(soccerService.gameList()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/soccer";
    }

    @RequestMapping(value = "zone/soccer/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", soccerService.getClosing(command, pageable));
        return "site/score/soccer";
    }

    @ResponseBody
    @RequestMapping(value = "zone/soccer/config", method = RequestMethod.POST)
    public SoccerDto.Config config() {
        return soccerService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/soccer/list", method = RequestMethod.POST)
    public List<SoccerDto.List> list(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody SoccerDto.Command command) {
        if (!ajax) {
            return new ArrayList<>();
        }
        return soccerService.gameList(command.getId());
    }

    @ResponseBody
    @RequestMapping(value = "zone/soccer/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return soccerGameService.betting(bet);
    }
}
