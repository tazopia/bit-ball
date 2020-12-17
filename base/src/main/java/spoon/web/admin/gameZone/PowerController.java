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
import spoon.gameZone.power.PowerConfig;
import spoon.gameZone.power.PowerDto;
import spoon.gameZone.power.service.PowerService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.powerController")
@RequestMapping(value = "#{config.pathAdmin}")
public class PowerController {

    private PowerService powerService;

    /**
     * 파워볼 설정
     */
    @RequestMapping(value = "zone/power/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getPower());
        return "admin/zone/power/config";
    }

    /**
     * 파워볼 설정 변경
     */
    @RequestMapping(value = "zone/power/config", method = RequestMethod.POST)
    public String config(PowerConfig powerConfig, RedirectAttributes ra) {
        boolean success = powerService.updateConfig(powerConfig);
        if (success) {
            ra.addFlashAttribute("message", "파워볼 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "파워볼 설정변경에 실패하였습니다.");
        }

        return "redirect:" + Config.getPathAdmin() + "/zone/power/config";
    }

    /**
     * 파워볼 진행
     */
    @RequestMapping(value = "zone/power/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", powerService.getComplete());
        map.addAttribute("config", ZoneConfig.getPower());
        return "admin/zone/power/complete";
    }

    /**
     * 파워볼 완료
     */
    @RequestMapping(value = "zone/power/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", powerService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getPower());
        return "admin/zone/power/closing";
    }

    /**
     * 파워볼 스코어 입력 폼
     */
    @RequestMapping(value = "zone/power/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", powerService.findScore(id));
        return "admin/zone/power/score";
    }

    /**
     * 파워볼 결과처리
     */
    @RequestMapping(value = "zone/power/score", method = RequestMethod.POST)
    public String score(PowerDto.Score score, RedirectAttributes ra) {
        boolean success = powerService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "파워볼 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "파워볼 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/power/score?id=" + score.getId();
    }

    /**
     * 파워볼 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/power/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return powerService.closingAllGame();
    }

}
