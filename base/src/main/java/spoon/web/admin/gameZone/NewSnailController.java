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
import spoon.gameZone.newSnail.NewSnailConfig;
import spoon.gameZone.newSnail.NewSnailDto;
import spoon.gameZone.newSnail.service.NewSnailService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.newSnailController")
@RequestMapping(value = "#{config.pathAdmin}")
public class NewSnailController {

    private NewSnailService newSnailService;


    /**
     * 다리다리 설정
     */
    @RequestMapping(value = "zone/new_snail/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getNewSnail());
        return "admin/zone/new_snail/config";
    }

    /**
     * 다리다리 설정 변경
     */
    @RequestMapping(value = "zone/new_snail/config", method = RequestMethod.POST)
    public String config(NewSnailConfig newSnailConfig, RedirectAttributes ra) {
        boolean success = newSnailService.updateConfig(newSnailConfig);
        if (success) {
            ra.addFlashAttribute("message", "NEW 달팽이 게임 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "NEW 달팽이 게임 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/new_snail/config";
    }

    /**
     * 다리다리 진행
     */
    @RequestMapping(value = "zone/new_snail/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", newSnailService.getComplete());
        map.addAttribute("config", ZoneConfig.getNewSnail());
        return "admin/zone/new_snail/complete";
    }

    /**
     * 다리다리 완료
     */
    @RequestMapping(value = "zone/new_snail/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", newSnailService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getNewSnail());
        return "admin/zone/new_snail/closing";
    }

    /**
     * 다리다리 스코어 입력 폼
     */
    @RequestMapping(value = "zone/new_snail/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", newSnailService.findScore(id));
        return "admin/zone/new_snail/score";
    }

    /**
     * 다리다리 결과처리
     */
    @RequestMapping(value = "zone/new_snail/score", method = RequestMethod.POST)
    public String score(NewSnailDto.Score score, RedirectAttributes ra) {
        boolean success = newSnailService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "NEW 달팽이 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "NEW 달팽이 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/new_snail/score?id=" + score.getId();
    }

    /**
     * 사다리 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/new_snail/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return newSnailService.closingAllGame();
    }

}
