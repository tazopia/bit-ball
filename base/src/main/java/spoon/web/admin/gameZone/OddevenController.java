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
import spoon.gameZone.oddeven.OddevenConfig;
import spoon.gameZone.oddeven.OddevenDto;
import spoon.gameZone.oddeven.service.OddevenService;
import spoon.support.web.AjaxResult;


@Slf4j
@AllArgsConstructor
@Controller("admin.oddevenController")
@RequestMapping(value = "#{config.pathAdmin}")
public class OddevenController {

    private OddevenService oddevenService;

    /**
     * 홀짝 설정
     */
    @RequestMapping(value = "zone/oddeven/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getOddeven());
        return "admin/zone/oddeven/config";
    }

    /**
     * 홀짝 설정 변경
     */
    @RequestMapping(value = "zone/oddeven/config", method = RequestMethod.POST)
    public String config(OddevenConfig oddevenConfig, RedirectAttributes ra) {
        boolean success = oddevenService.updateConfig(oddevenConfig);
        if (success) {
            ra.addFlashAttribute("message", "홀짝 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "홀짝 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/oddeven/config";
    }

    /**
     * 홀짝 진행
     */
    @RequestMapping(value = "zone/oddeven/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", oddevenService.getComplete());
        map.addAttribute("config", ZoneConfig.getOddeven());
        return "admin/zone/oddeven/complete";
    }

    /**
     * 홀짝 완료
     */
    @RequestMapping(value = "zone/oddeven/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", oddevenService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getOddeven());
        return "admin/zone/oddeven/closing";
    }

    /**
     * 홀짝 스코어 입력 폼
     */
    @RequestMapping(value = "zone/oddeven/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", oddevenService.findScore(id));
        return "admin/zone/oddeven/score";
    }

    /**
     * 홀짝 결과처리
     */
    @RequestMapping(value = "zone/oddeven/score", method = RequestMethod.POST)
    public String score(OddevenDto.Score score, RedirectAttributes ra) {
        boolean success = oddevenService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "홀짝 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "홀짝 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/oddeven/score?id=" + score.getId();
    }

    /**
     * 홀짝 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/oddeven/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return oddevenService.closingAllGame();
    }

}
