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
import spoon.gameZone.powerLadder.PowerLadderDto;
import spoon.gameZone.powerLadder.PowerLadderScore;
import spoon.gameZone.powerLadder.service.PowerLadderGameService;
import spoon.gameZone.powerLadder.service.PowerLadderService;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class PowerLadderController {

    private PowerLadderService powerLadderService;

    private PowerLadderGameService powerLadderGameService;

    private GameZoneBetService gameZoneBetService;

    @RequestMapping(value = "zone/power_ladder", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(powerLadderService.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(powerLadderService.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.POWER_LADDER)));
        //map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/power_ladder";
    }

    @RequestMapping(value = "zone/power_ladder/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", powerLadderService.getClosing(command, pageable));
        return "site/score/power_ladder";
    }

    @ResponseBody
    @RequestMapping(value = "zone/power_ladder/config", method = RequestMethod.POST)
    public PowerLadderDto.Config config() {
        return powerLadderService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/power_ladder/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return powerLadderGameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/power_ladder/score", method = RequestMethod.POST)
    public Iterable<PowerLadderScore> getResult() {
        return powerLadderService.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/power_ladder/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.POWER_LADDER);
    }

}
