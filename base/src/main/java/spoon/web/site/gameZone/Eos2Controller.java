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
import spoon.gameZone.eos2.domain.Eos2Dto;
import spoon.gameZone.eos2.domain.Eos2Score;
import spoon.gameZone.eos2.service.Eos2GameService;
import spoon.gameZone.eos2.service.Eos2Service;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class Eos2Controller {

    private final Eos2Service eos2Service;

    private final Eos2GameService eos2GameService;

    private final GameZoneBetService gameZoneBetService;

    @RequestMapping(value = "zone/eos2", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(eos2Service.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(eos2Service.getScore()));
        map.addAttribute("bet", JsonUtils.toString(gameZoneBetService.zoneBetting(MenuCode.EOS2)));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/eos2";
    }

    @RequestMapping(value = "zone/eos2/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", eos2Service.getClosing(command, pageable));
        return "site/score/eos2";
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos2/config", method = RequestMethod.POST)
    public Eos2Dto.Config config() {
        return eos2Service.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos2/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return eos2GameService.betting(bet);
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos2/score", method = RequestMethod.POST)
    public Iterable<Eos2Score> getResult() {
        return eos2Service.getScore();
    }

    @ResponseBody
    @RequestMapping(value = "zone/eos2/bet", method = RequestMethod.POST)
    public List<ZoneBetDto> getBet() {
        return gameZoneBetService.zoneBetting(MenuCode.EOS2);
    }
}
