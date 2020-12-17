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
import spoon.gameZone.snail.SnailConfig;
import spoon.gameZone.snail.SnailDto;
import spoon.gameZone.snail.service.SnailService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.snailController")
@RequestMapping(value = "#{config.pathAdmin}")
public class SnailController {

    private SnailService snailService;

    /**
     * 달팽이 설정
     */
    @RequestMapping(value = "zone/snail/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getSnail());
        return "admin/zone/snail/config";
    }

    /**
     * 달팽이 설정 변경
     */
    @RequestMapping(value = "zone/snail/config", method = RequestMethod.POST)
    public String config(SnailConfig snailConfig, RedirectAttributes ra) {
        boolean success = snailService.updateConfig(snailConfig);
        if (success) {
            ra.addFlashAttribute("message", "달팽이 게임설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "달팽이 게임설정 변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/snail/config";
    }

    /**
     * 달팽이 진행
     */
    @RequestMapping(value = "zone/snail/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", snailService.getComplete());
        map.addAttribute("config", ZoneConfig.getSnail());
        return "admin/zone/snail/complete";
    }

    /**
     * 달팽이 완료
     */
    @RequestMapping(value = "zone/snail/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", snailService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getSnail());
        return "admin/zone/snail/closing";
    }

    /**
     * 달팽이 스코어 입력 폼
     */
    @RequestMapping(value = "zone/snail/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", snailService.findScore(id));
        return "admin/zone/snail/score";
    }

    /**
     * 달팽이 결과처리
     */
    @RequestMapping(value = "zone/snail/score", method = RequestMethod.POST)
    public String score(SnailDto.Score score, RedirectAttributes ra) {
        boolean success = snailService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "달팽이 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "달팽이 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/snail/score?id=" + score.getId();
    }

    /**
     * 달팽이 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/snail/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return snailService.closingAllGame();
    }

}
