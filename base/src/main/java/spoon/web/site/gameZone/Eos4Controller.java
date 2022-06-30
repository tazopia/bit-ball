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
import spoon.gameZone.eos4.domain.Eos4Dto;
import spoon.gameZone.eos4.domain.Eos4Score;
import spoon.gameZone.eos4.service.Eos4GameService;
import spoon.gameZone.eos4.service.Eos4Service;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class Eos4Controller {

    private final Eos4Service eos4Service;

    private final Eos4GameService eos4GameService;

    private final GameZoneBetService gameZoneBetService;

    @RequestMapping(value = "zone/eos4", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(eos4Service.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(eos4Service.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.EOS4)));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/eos4";
    }

    @RequestMapping(value = "zone/eos4/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", eos4Service.getClosing(command, pageable));
        return "site/score/eos4";
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos4/config", method = RequestMethod.POST)
    public Eos4Dto.Config config() {
        return eos4Service.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos4/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return eos4GameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos4/score", method = RequestMethod.POST)
    public Iterable<Eos4Score> getResult() {
        return eos4Service.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos4/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.EOS4);
    }
}
