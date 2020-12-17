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
import spoon.gameZone.lowhi.LowhiConfig;
import spoon.gameZone.lowhi.LowhiDto;
import spoon.gameZone.lowhi.service.LowhiService;
import spoon.support.web.AjaxResult;


@Slf4j
@AllArgsConstructor
@Controller("admin.lowhiController")
@RequestMapping(value = "#{config.pathAdmin}")
public class LowhiController {

    private LowhiService lowhiService;

    /**
     * 로하이 설정
     */
    @RequestMapping(value = "zone/lowhi/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getLowhi());
        return "admin/zone/lowhi/config";
    }

    /**
     * 로하이 설정 변경
     */
    @RequestMapping(value = "zone/lowhi/config", method = RequestMethod.POST)
    public String config(LowhiConfig lowhiConfig, RedirectAttributes ra) {
        boolean success = lowhiService.updateConfig(lowhiConfig);
        if (success) {
            ra.addFlashAttribute("message", "로하이 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "로하이 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/lowhi/config";
    }

    /**
     * 로하이 진행
     */
    @RequestMapping(value = "zone/lowhi/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", lowhiService.getComplete());
        map.addAttribute("config", ZoneConfig.getLowhi());
        return "admin/zone/lowhi/complete";
    }

    /**
     * 로하이 완료
     */
    @RequestMapping(value = "zone/lowhi/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", lowhiService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getLowhi());
        return "admin/zone/lowhi/closing";
    }

    /**
     * 로하이 스코어 입력 폼
     */
    @RequestMapping(value = "zone/lowhi/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", lowhiService.findScore(id));
        return "admin/zone/lowhi/score";
    }

    /**
     * 로하이 결과처리
     */
    @RequestMapping(value = "zone/lowhi/score", method = RequestMethod.POST)
    public String score(LowhiDto.Score score, RedirectAttributes ra) {
        boolean success = lowhiService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "로하이 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "로하이 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/lowhi/score?id=" + score.getId();
    }

    /**
     * 로하이 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/lowhi/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return lowhiService.closingAllGame();
    }

}
