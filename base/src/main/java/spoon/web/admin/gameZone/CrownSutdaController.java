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
import spoon.gameZone.crownSutda.CrownSutdaConfig;
import spoon.gameZone.crownSutda.CrownSutdaDto;
import spoon.gameZone.crownSutda.service.CrownSutdaService;
import spoon.support.web.AjaxResult;


@Slf4j
@AllArgsConstructor
@Controller("admin.crownSutdaController")
@RequestMapping(value = "#{config.pathAdmin}")
public class CrownSutdaController {

    private CrownSutdaService crownSutdaService;

    /**
     * 섰다 설정
     */
    @RequestMapping(value = "zone/cw_sutda/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getCrownSutda());
        return "admin/zone/cw_sutda/config";
    }

    /**
     * 섰다 설정 변경
     */
    @RequestMapping(value = "zone/cw_sutda/config", method = RequestMethod.POST)
    public String config(CrownSutdaConfig crownSutdaConfig, RedirectAttributes ra) {
        boolean success = crownSutdaService.updateConfig(crownSutdaConfig);
        if (success) {
            ra.addFlashAttribute("message", "섰다 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "섰다 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/cw_sutda/config";
    }

    /**
     * 섰다 진행
     */
    @RequestMapping(value = "zone/cw_sutda/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", crownSutdaService.getComplete());
        map.addAttribute("config", ZoneConfig.getCrownSutda());
        return "admin/zone/cw_sutda/complete";
    }

    /**
     * 섰다 완료
     */
    @RequestMapping(value = "zone/cw_sutda/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", crownSutdaService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getCrownSutda());
        return "admin/zone/cw_sutda/closing";
    }

    /**
     * 섰다 스코어 입력 폼
     */
    @RequestMapping(value = "zone/cw_sutda/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", crownSutdaService.findScore(id));
        return "admin/zone/cw_sutda/score";
    }

    /**
     * 섰다 결과처리
     */
    @RequestMapping(value = "zone/cw_sutda/score", method = RequestMethod.POST)
    public String score(CrownSutdaDto.Score score, RedirectAttributes ra) {
        boolean success = crownSutdaService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "섰다 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "섰다 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/cw_sutda/score?id=" + score.getId();
    }

    /**
     * 섰다 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/cw_sutda/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return crownSutdaService.closingAllGame();
    }

}
