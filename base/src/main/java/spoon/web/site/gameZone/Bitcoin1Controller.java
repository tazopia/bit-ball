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
import spoon.gameZone.bitcoin1.domain.Bitcoin1Dto;
import spoon.gameZone.bitcoin1.domain.Bitcoin1Score;
import spoon.gameZone.bitcoin1.service.Bitcoin1GameService;
import spoon.gameZone.bitcoin1.service.Bitcoin1Service;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class Bitcoin1Controller {

    private final Bitcoin1Service bitcoin1Service;

    private final GameZoneBetService gameZoneBetService;

    private final Bitcoin1GameService bitcoin1GameService;

    @RequestMapping(value = "zone/bitcoin1", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(bitcoin1Service.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(bitcoin1Service.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.BITCOIN1)));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/bitcoin1";
    }

    @RequestMapping(value = "zone/bitcoin1/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", bitcoin1Service.getClosing(command, pageable));
        return "site/score/bitcoin1";
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin1/config", method = RequestMethod.POST)
    public Bitcoin1Dto.Config config() {
        return bitcoin1Service.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin1/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return bitcoin1GameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin1/score", method = RequestMethod.POST)
    public Iterable<Bitcoin1Score> getResult() {
        return bitcoin1Service.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin1/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.BITCOIN1);
    }
}
