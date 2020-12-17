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
import spoon.gameZone.baccarat.BaccaratConfig;
import spoon.gameZone.baccarat.BaccaratDto;
import spoon.gameZone.baccarat.service.BaccaratService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.baccaratController")
@RequestMapping(value = "#{config.pathAdmin}")
public class BaccaratController {

    private BaccaratService baccaratService;

    /**
     * 바카라 설정
     */
    @RequestMapping(value = "zone/baccarat/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getBaccarat());
        return "admin/zone/baccarat/config";
    }

    /**
     * 바카라 설정 변경
     */
    @RequestMapping(value = "zone/baccarat/config", method = RequestMethod.POST)
    public String config(BaccaratConfig baccaratConfig, RedirectAttributes ra) {
        boolean success = baccaratService.updateConfig(baccaratConfig);
        if (success) {
            ra.addFlashAttribute("message", "바카라 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "바카라 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/baccarat/config";
    }

    /**
     * 바카라 진행
     */
    @RequestMapping(value = "zone/baccarat/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", baccaratService.getComplete());
        map.addAttribute("config", ZoneConfig.getBaccarat());
        return "admin/zone/baccarat/complete";
    }

    /**
     * 바카라 완료
     */
    @RequestMapping(value = "zone/baccarat/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", baccaratService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getBaccarat());
        return "admin/zone/baccarat/closing";
    }

    /**
     * 바카라 스코어 입력 폼
     */
    @RequestMapping(value = "zone/baccarat/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", baccaratService.findScore(id));
        return "admin/zone/baccarat/score";
    }

    /**
     * 바카라 결과처리
     */
    @RequestMapping(value = "zone/baccarat/score", method = RequestMethod.POST)
    public String score(BaccaratDto.Score score, RedirectAttributes ra) {
        boolean success = baccaratService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "바카라 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "바카라 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/baccarat/score?id=" + score.getId();
    }

    /**
     * 바카라 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/baccarat/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return baccaratService.closingAllGame();
    }

}
