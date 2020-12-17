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
import spoon.gameZone.soccer.SoccerConfig;
import spoon.gameZone.soccer.SoccerDto;
import spoon.gameZone.soccer.service.SoccerService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.soccerController")
@RequestMapping(value = "#{config.pathAdmin}")
public class SoccerController {

    private SoccerService soccerService;

    /**
     * 가상축구 설정
     */
    @RequestMapping(value = "zone/soccer/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getSoccer());
        return "admin/zone/soccer/config";
    }

    /**
     * 가상축구 설정 변경
     */
    @RequestMapping(value = "zone/soccer/config", method = RequestMethod.POST)
    public String config(SoccerConfig soccerConfig, RedirectAttributes ra) {
        boolean success = soccerService.updateConfig(soccerConfig);
        if (success) {
            ra.addFlashAttribute("message", "가상축구 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "가상축구 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/soccer/config";
    }

    /**
     * 가상축구 진행
     */
    @RequestMapping(value = "zone/soccer/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", soccerService.getComplete());
        map.addAttribute("config", ZoneConfig.getSoccer());
        return "admin/zone/soccer/complete";
    }

    /**
     * 가상축구 완료
     */
    @RequestMapping(value = "zone/soccer/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", soccerService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getSoccer());
        return "admin/zone/soccer/closing";
    }

    /**
     * 가상축구 스코어 입력 폼
     */
    @RequestMapping(value = "zone/soccer/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", soccerService.findScore(id));
        return "admin/zone/soccer/score";
    }

    /**
     * 가상축구 결과처리
     */
    @RequestMapping(value = "zone/soccer/score", method = RequestMethod.POST)
    public String score(SoccerDto.Score score, RedirectAttributes ra) {
        boolean success = soccerService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "가상축구 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "가상축구 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/soccer/score?id=" + score.getId();
    }

    /**
     * 가상축구 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/soccer/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return soccerService.closingAllGame();
    }

}
