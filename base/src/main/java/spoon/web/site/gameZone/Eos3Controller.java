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
import spoon.gameZone.eos3.domain.Eos3Dto;
import spoon.gameZone.eos3.domain.Eos3Score;
import spoon.gameZone.eos3.service.Eos3GameService;
import spoon.gameZone.eos3.service.Eos3Service;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class Eos3Controller {

    private final Eos3Service eos3Service;

    private final Eos3GameService eos3GameService;

    private final GameZoneBetService gameZoneBetService;

    @RequestMapping(value = "zone/eos3", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(eos3Service.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(eos3Service.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.EOS3)));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/eos3";
    }

    @RequestMapping(value = "zone/eos3/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", eos3Service.getClosing(command, pageable));
        return "site/score/eos3";
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos3/config", method = RequestMethod.POST)
    public Eos3Dto.Config config() {
        return eos3Service.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos3/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return eos3GameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos3/score", method = RequestMethod.POST)
    public Iterable<Eos3Score> getResult() {
        return eos3Service.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos3/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.EOS3);
    }
}
