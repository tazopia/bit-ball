package spoon.gameZone.keno.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import spoon.common.utils.JsonUtils;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.keno.domain.KenoDto;
import spoon.gameZone.keno.domain.KenoScore;
import spoon.gameZone.keno.service.KenoGameService;
import spoon.gameZone.keno.service.KenoService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class KenoController {

    private final KenoService kenoService;

    private final KenoGameService kenoGameService;

    @GetMapping(value = "zone/keno")
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(kenoService.gameConfig()));
        map.addAttribute("score", JsonUtils.toString(kenoService.getScore()));
        //map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/keno";
    }

    @GetMapping(value = "zone/keno/score")
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", kenoService.getClosing(command, pageable));
        return "site/score/keno";
    }

    @ResponseBody
    @PostMapping(value = "zone/keno/config")
    public KenoDto.Config config() {
        return kenoService.gameConfig();
    }

    @ResponseBody
    @PostMapping(value = "zone/keno/betting")
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return kenoGameService.betting(bet);
    }

    @ResponseBody
    @PostMapping(value = "zone/keno/score")
    public Iterable<KenoScore> getResult() {
        return kenoService.getScore();
    }

}
