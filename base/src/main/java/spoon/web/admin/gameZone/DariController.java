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
import spoon.gameZone.dari.DariConfig;
import spoon.gameZone.dari.DariDto;
import spoon.gameZone.dari.service.DariService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.dariController")
@RequestMapping(value = "#{config.pathAdmin}")
public class DariController {

    private DariService dariService;


    /**
     * 다리다리 설정
     */
    @RequestMapping(value = "zone/dari/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getDari());
        return "admin/zone/dari/config";
    }

    /**
     * 다리다리 설정 변경
     */
    @RequestMapping(value = "zone/dari/config", method = RequestMethod.POST)
    public String config(DariConfig dariConfig, RedirectAttributes ra) {
        boolean success = dariService.updateConfig(dariConfig);
        if (success) {
            ra.addFlashAttribute("message", "다리게임 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "다리게임 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/dari/config";
    }

    /**
     * 다리다리 진행
     */
    @RequestMapping(value = "zone/dari/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", dariService.getComplete());
        map.addAttribute("config", ZoneConfig.getDari());
        return "admin/zone/dari/complete";
    }

    /**
     * 다리다리 완료
     */
    @RequestMapping(value = "zone/dari/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", dariService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getDari());
        return "admin/zone/dari/closing";
    }

    /**
     * 다리다리 스코어 입력 폼
     */
    @RequestMapping(value = "zone/dari/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", dariService.findScore(id));
        return "admin/zone/dari/score";
    }

    /**
     * 다리다리 결과처리
     */
    @RequestMapping(value = "zone/dari/score", method = RequestMethod.POST)
    public String score(DariDto.Score score, RedirectAttributes ra) {
        boolean success = dariService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "다리다리 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "다리다리 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/dari/score?id=" + score.getId();
    }

    /**
     * 사다리 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/dari/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return dariService.closingAllGame();
    }

}
