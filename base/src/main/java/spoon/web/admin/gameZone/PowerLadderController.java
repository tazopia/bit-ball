package spoon.web.admin.gameZone;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.powerLadder.PowerLadderConfig;
import spoon.gameZone.powerLadder.PowerLadderDto;
import spoon.gameZone.powerLadder.service.PowerLadderService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.PowerLadderController")
@RequestMapping(value = "#{config.pathAdmin}")
public class PowerLadderController {

    private PowerLadderService powerLadderService;

    /**
     * 파워사다리 설정
     */
    @RequestMapping(value = "zone/power_ladder/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getPowerLadder());
        return "admin/zone/power_ladder/config";
    }

    /**
     * 파워사다리 설정 변경
     */
    @RequestMapping(value = "zone/power_ladder/config", method = RequestMethod.POST)
    public String config(PowerLadderConfig powerLadderConfig, RedirectAttributes ra) {
        boolean success = powerLadderService.updateConfig(powerLadderConfig);
        if (success) {
            ra.addFlashAttribute("message", "파워사다리 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "파워사다리 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/power_ladder/config";
    }

    /**
     * 파워사다리 진행
     */
    @RequestMapping(value = "zone/power_ladder/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", powerLadderService.getComplete());
        map.addAttribute("config", ZoneConfig.getPowerLadder());
        return "admin/zone/power_ladder/complete";
    }

    /**
     * 파워사다리 완료
     */
    @RequestMapping(value = "zone/power_ladder/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", powerLadderService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getPowerLadder());
        return "admin/zone/power_ladder/closing";
    }

    /**
     * 사다리 스코어 입력 폼
     */
    @RequestMapping(value = "zone/power_ladder/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", powerLadderService.findScore(id));
        return "admin/zone/power_ladder/score";
    }

    /**
     * 파워사다리 스코어 결과처리
     */
    @RequestMapping(value = "zone/power_ladder/score", method = RequestMethod.POST)
    public String score(PowerLadderDto.Score score, RedirectAttributes ra) {
        boolean success = powerLadderService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "파워사다리 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "파워사다리 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/power_ladder/score?id=" + score.getId();
    }

    /**
     * 파워사다리 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/power_ladder/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return powerLadderService.closingAllGame();
    }

}
