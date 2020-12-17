package spoon.web.site.gameZone;

import lombok.RequiredArgsConstructor;
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
import spoon.gameZone.bitcoin5.domain.Bitcoin5Dto;
import spoon.gameZone.bitcoin5.domain.Bitcoin5Score;
import spoon.gameZone.bitcoin5.service.Bitcoin5GameService;
import spoon.gameZone.bitcoin5.service.Bitcoin5Service;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class Bitcoin5Controller {

    private final Bitcoin5Service bitcoin5Service;

    private final GameZoneBetService gameZoneBetService;

    private final Bitcoin5GameService bitcoin5GameService;

    @RequestMapping(value = "zone/bitcoin5", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(bitcoin5Service.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(bitcoin5Service.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.BITCOIN5)));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/bitcoin5";
    }

    @RequestMapping(value = "zone/bitcoin5/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", bitcoin5Service.getClosing(command, pageable));
        return "site/score/bitcoin5";
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin5/config", method = RequestMethod.POST)
    public Bitcoin5Dto.Config config() {
        return bitcoin5Service.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin5/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return bitcoin5GameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin5/score", method = RequestMethod.POST)
    public Iterable<Bitcoin5Score> getResult() {
        return bitcoin5Service.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin5/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.BITCOIN5);
    }
}
