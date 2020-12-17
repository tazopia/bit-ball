package spoon.web.site.gameZone;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import spoon.bet.domain.ZoneBetDto;
import spoon.bet.service.GameZoneBetService;
import spoon.common.utils.JsonUtils;
import spoon.game.domain.MenuCode;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.power.PowerDto;
import spoon.gameZone.power.PowerScore;
import spoon.gameZone.power.service.PowerGameService;
import spoon.gameZone.power.service.PowerService;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class PowerController {

    private PowerService powerService;

    private PowerGameService powerGameService;

    private GameZoneBetService gameZoneBetService;

    @RequestMapping(value = "zone/power", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(powerService.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(powerService.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.POWER)));
        //map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/power";
    }

    @RequestMapping(value = "zone/power/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", powerService.getClosing(command, pageable));
        return "site/score/power";
    }

    @ResponseBody
    @RequestMapping(value = "zone/power/config", method = RequestMethod.POST)
    public PowerDto.Config config() {
        return powerService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/power/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return powerGameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/power/score", method = RequestMethod.POST)
    public Iterable<PowerScore> getResult() {
        return powerService.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/power/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.POWER);
    }

}
