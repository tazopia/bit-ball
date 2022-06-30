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
import spoon.gameZone.eos5.domain.Eos5Dto;
import spoon.gameZone.eos5.domain.Eos5Score;
import spoon.gameZone.eos5.service.Eos5GameService;
import spoon.gameZone.eos5.service.Eos5Service;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class Eos5Controller {

    private final Eos5Service eos5Service;

    private final Eos5GameService eos5GameService;

    private final GameZoneBetService gameZoneBetService;

    @RequestMapping(value = "zone/eos5", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(eos5Service.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(eos5Service.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.EOS5)));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/eos5";
    }

    @RequestMapping(value = "zone/eos5/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", eos5Service.getClosing(command, pageable));
        return "site/score/eos5";
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos5/config", method = RequestMethod.POST)
    public Eos5Dto.Config config() {
        return eos5Service.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos5/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return eos5GameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos5/score", method = RequestMethod.POST)
    public Iterable<Eos5Score> getResult() {
        return eos5Service.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos5/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.EOS5);
    }
}
