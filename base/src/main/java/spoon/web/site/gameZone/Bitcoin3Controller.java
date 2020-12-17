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
import spoon.gameZone.bitcoin3.domain.Bitcoin3Dto;
import spoon.gameZone.bitcoin3.domain.Bitcoin3Score;
import spoon.gameZone.bitcoin3.service.Bitcoin3GameService;
import spoon.gameZone.bitcoin3.service.Bitcoin3Service;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class Bitcoin3Controller {

    private final Bitcoin3Service bitcoin3Service;

    private final GameZoneBetService gameZoneBetService;

    private final Bitcoin3GameService bitcoin3GameService;

    @RequestMapping(value = "zone/bitcoin3", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(bitcoin3Service.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(bitcoin3Service.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.BITCOIN3)));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/bitcoin3";
    }

    @RequestMapping(value = "zone/bitcoin3/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", bitcoin3Service.getClosing(command, pageable));
        return "site/score/bitcoin3";
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin3/config", method = RequestMethod.POST)
    public Bitcoin3Dto.Config config() {
        return bitcoin3Service.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin3/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return bitcoin3GameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin3/score", method = RequestMethod.POST)
    public Iterable<Bitcoin3Score> getResult() {
        return bitcoin3Service.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/bitcoin3/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.BITCOIN3);
    }
}
