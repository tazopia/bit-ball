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
import spoon.gameZone.KenoLadder.KenoLadderDto;
import spoon.gameZone.KenoLadder.KenoLadderScore;
import spoon.gameZone.KenoLadder.service.KenoLadderGameService;
import spoon.gameZone.KenoLadder.service.KenoLadderService;
import spoon.gameZone.ZoneDto;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class KenoLadderController {

    private KenoLadderService kenoLadderService;

    private KenoLadderGameService kenoLadderGameService;

    private GameZoneBetService gameZoneBetService;

    @RequestMapping(value = "zone/keno_ladder", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(kenoLadderService.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(kenoLadderService.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.KENO_LADDER)));
        //map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/keno_ladder";
    }

    @RequestMapping(value = "zone/keno_ladder/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", kenoLadderService.getClosing(command, pageable));
        return "site/score/keno_ladder";
    }

    @ResponseBody
    @RequestMapping(value = "zone/keno_ladder/config", method = RequestMethod.POST)
    public KenoLadderDto.Config config() {
        return kenoLadderService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/keno_ladder/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return kenoLadderGameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/keno_ladder/score", method = RequestMethod.POST)
    public Iterable<KenoLadderScore> getResult() {
        return kenoLadderService.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/keno_ladder/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.KENO_LADDER);
    }

}
