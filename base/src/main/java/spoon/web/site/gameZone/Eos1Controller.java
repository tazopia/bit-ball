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
import spoon.gameZone.eos1.domain.Eos1Dto;
import spoon.gameZone.eos1.domain.Eos1Score;
import spoon.gameZone.eos1.service.Eos1GameService;
import spoon.gameZone.eos1.service.Eos1Service;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class Eos1Controller {

    private final Eos1Service eos1Service;

    private final Eos1GameService eos1GameService;

    private final GameZoneBetService gameZoneBetService;

    @RequestMapping(value = "zone/eos1", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(eos1Service.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(eos1Service.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.EOS1)));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/eos1";
    }

    @RequestMapping(value = "zone/eos1/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", eos1Service.getClosing(command, pageable));
        return "site/score/eos1";
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos1/config", method = RequestMethod.POST)
    public Eos1Dto.Config config() {
        return eos1Service.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos1/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return eos1GameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos1/score", method = RequestMethod.POST)
    public Iterable<Eos1Score> getResult() {
        return eos1Service.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos1/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.EOS1);
    }
}
