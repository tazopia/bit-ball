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
import spoon.gameZone.aladdin.AladdinConfig;
import spoon.gameZone.aladdin.AladdinDto;
import spoon.gameZone.aladdin.service.AladdinService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.aladdinController")
@RequestMapping(value = "#{config.pathAdmin}")
public class AladdinController {

    private AladdinService aladdinService;

    /**
     * 알라딘 설정
     */
    @RequestMapping(value = "zone/aladdin/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getAladdin());
        return "admin/zone/aladdin/config";
    }

    /**
     * 알라딘 설정 변경
     */
    @RequestMapping(value = "zone/aladdin/config", method = RequestMethod.POST)
    public String config(AladdinConfig aladdinConfig, RedirectAttributes ra) {
        boolean success = aladdinService.updateConfig(aladdinConfig);
        if (success) {
            ra.addFlashAttribute("message", "알라딘 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "알라딘 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/aladdin/config";
    }

    /**
     * 알라딘 진행
     */
    @RequestMapping(value = "zone/aladdin/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", aladdinService.getComplete());
        map.addAttribute("config", ZoneConfig.getAladdin());
        return "admin/zone/aladdin/complete";
    }

    /**
     * 알라딘 완료
     */
    @RequestMapping(value = "zone/aladdin/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", aladdinService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getAladdin());
        return "admin/zone/aladdin/closing";
    }

    /**
     * 알라딘 스코어 입력 폼
     */
    @RequestMapping(value = "zone/aladdin/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", aladdinService.findScore(id));
        return "admin/zone/aladdin/score";
    }

    /**
     * 알라딘 결과처리
     */
    @RequestMapping(value = "zone/aladdin/score", method = RequestMethod.POST)
    public String score(AladdinDto.Score score, RedirectAttributes ra) {
        boolean success = aladdinService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "알라딘 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "알라딘 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/aladdin/score?id=" + score.getId();
    }

    /**
     * 알라딘 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/aladdin/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return aladdinService.closingAllGame();
    }

}
