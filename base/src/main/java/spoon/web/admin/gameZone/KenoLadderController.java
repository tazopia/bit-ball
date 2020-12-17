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
import spoon.gameZone.KenoLadder.KenoLadderConfig;
import spoon.gameZone.KenoLadder.KenoLadderDto;
import spoon.gameZone.KenoLadder.service.KenoLadderService;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneDto;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.kenoLadderController")
@RequestMapping(value = "#{config.pathAdmin}")
public class KenoLadderController {

    private KenoLadderService kenoLadderService;

    /**
     * 키노사다리 설정
     */
    @RequestMapping(value = "zone/keno_ladder/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getKenoLadder());
        return "admin/zone/keno_ladder/config";
    }

    /**
     * 키노사다리 설정 변경
     */
    @RequestMapping(value = "zone/keno_ladder/config", method = RequestMethod.POST)
    public String config(KenoLadderConfig kenoLadderConfig, RedirectAttributes ra) {
        boolean success = kenoLadderService.updateConfig(kenoLadderConfig);
        if (success) {
            ra.addFlashAttribute("message", "키노사다리게임 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "키노사다리게임 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/keno_ladder/config";
    }

    /**
     * 키노사다리 진행
     */
    @RequestMapping(value = "zone/keno_ladder/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", kenoLadderService.getComplete());
        map.addAttribute("config", ZoneConfig.getKenoLadder());
        return "admin/zone/keno_ladder/complete";
    }

    /**
     * 키노사다리 완료
     */
    @RequestMapping(value = "zone/keno_ladder/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", kenoLadderService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getKenoLadder());
        return "admin/zone/keno_ladder/closing";
    }

    /**
     * 키노사다리 스코어 입력 폼
     */
    @RequestMapping(value = "zone/keno_ladder/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", kenoLadderService.findScore(id));
        return "admin/zone/keno_ladder/score";
    }

    /**
     * 키노사다리 스코어 결과처리
     */
    @RequestMapping(value = "zone/keno_ladder/score", method = RequestMethod.POST)
    public String score(KenoLadderDto.Score score, RedirectAttributes ra) {
        boolean success = kenoLadderService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "키노사다리 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "키노사다리 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/keno_ladder/score?id=" + score.getId();
    }

    /**
     * 키노사다리 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/keno_ladder/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return kenoLadderService.closingAllGame();
    }

}
