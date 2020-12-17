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
import spoon.gameZone.luck.LuckConfig;
import spoon.gameZone.luck.LuckDto;
import spoon.gameZone.luck.service.LuckService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.luckController")
@RequestMapping(value = "#{config.pathAdmin}")
public class LuckController {

    private LuckService luckService;

    /**
     * 세븐럭 설정
     */
    @RequestMapping(value = "zone/luck/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getLuck());
        return "admin/zone/luck/config";
    }

    /**
     * 세븐럭 설정 변경
     */
    @RequestMapping(value = "zone/luck/config", method = RequestMethod.POST)
    public String config(LuckConfig luckConfig, RedirectAttributes ra) {
        boolean success = luckService.updateConfig(luckConfig);
        if (success) {
            ra.addFlashAttribute("message", "세븐럭 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "세븐럭 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/luck/config";
    }

    /**
     * 세븐럭 진행
     */
    @RequestMapping(value = "zone/luck/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", luckService.getComplete());
        map.addAttribute("config", ZoneConfig.getLuck());
        return "admin/zone/luck/complete";
    }

    /**
     * 세븐럭 완료
     */
    @RequestMapping(value = "zone/luck/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", luckService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getBaccarat());
        return "admin/zone/luck/closing";
    }

    /**
     * 세븐럭 스코어 입력 폼
     */
    @RequestMapping(value = "zone/luck/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", luckService.findScore(id));
        return "admin/zone/luck/score";
    }

    /**
     * 세븐럭 결과처리
     */
    @RequestMapping(value = "zone/luck/score", method = RequestMethod.POST)
    public String score(LuckDto.Score score, RedirectAttributes ra) {
        boolean success = luckService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "세븐럭 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "세븐럭 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/luck/score?id=" + score.getId();
    }

    /**
     * 세븐럭 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/luck/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return luckService.closingAllGame();
    }

}
