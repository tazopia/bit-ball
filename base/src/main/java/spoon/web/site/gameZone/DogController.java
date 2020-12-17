package spoon.web.site.gameZone;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import spoon.common.utils.JsonUtils;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.dog.DogDto;
import spoon.gameZone.dog.service.DogGameService;
import spoon.gameZone.dog.service.DogService;
import spoon.member.domain.UserBetInfo;
import spoon.support.web.AjaxResult;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping(value = "#{config.pathSite}")
public class DogController {

    private DogService dogService;

    private DogGameService dogGameService;

    @RequestMapping(value = "zone/dog", method = RequestMethod.GET)
    public String zone(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(dogService.gameConfig()));
        map.addAttribute("list", JsonUtils.toString(dogService.gameList()));
        map.addAttribute("betInfo", new UserBetInfo());
        return "site/zone/dog";
    }

    @RequestMapping(value = "zone/dog/score", method = RequestMethod.GET)
    public String score(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                        @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", dogService.getClosing(command, pageable));
        return "site/score/dog";
    }

    @ResponseBody
    @RequestMapping(value = "zone/dog/config", method = RequestMethod.POST)
    public DogDto.Config config() {
        return dogService.gameConfig();
    }

    @ResponseBody
    @RequestMapping(value = "zone/dog/list", method = RequestMethod.POST)
    public List<DogDto.List> list(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody DogDto.Command command) {
        if (!ajax) {
            return new ArrayList<>();
        }
        return dogService.gameList(command.getId());
    }

    @ResponseBody
    @RequestMapping(value = "zone/dog/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody ZoneDto.Bet bet) {
        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }
        return dogGameService.betting(bet);
    }
}
