package spoon.gameZone.keno.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.keno.domain.KenoConfig;
import spoon.gameZone.keno.domain.KenoDto;
import spoon.gameZone.keno.service.KenoService;
import spoon.support.web.AjaxResult;

@Slf4j
@RequiredArgsConstructor
@Controller("admin.kenoAdminController")
@RequestMapping(value = "#{config.pathAdmin}")
public class KenoAdminController {

    private final KenoService kenoService;

    /**
     * 스피드키노 설정
     */
    @GetMapping(value = "zone/keno/config")
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getKeno());
        return "admin/zone/keno/config";
    }

    /**
     * 스피드키노 설정 변경
     */
    @PostMapping(value = "zone/keno/config")
    public String config(KenoConfig kenoConfig, RedirectAttributes ra) {
        boolean success = kenoService.updateConfig(kenoConfig);
        if (success) {
            ra.addFlashAttribute("message", "스피드키노 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "스피드키노 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/keno/config";
    }

    /**
     * 스피드키노 진행
     */
    @GetMapping(value = "zone/keno/complete")
    public String complete(ModelMap map) {
        map.addAttribute("list", kenoService.getComplete());
        map.addAttribute("config", ZoneConfig.getKeno());
        return "admin/zone/keno/complete";
    }

    /**
     * 스피드키노 완료
     */
    @GetMapping(value = "zone/keno/closing")
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "sdate") Pageable pageable) {
        map.addAttribute("page", kenoService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getKeno());
        return "admin/zone/keno/closing";
    }

    /**
     * 스피드키노 스코어 입력 폼
     */
    @GetMapping(value = "zone/keno/score")
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", kenoService.findScore(id));
        return "admin/zone/keno/score";
    }

    /**
     * 스피드키노 스코어 결과처리
     */
    @PostMapping(value = "zone/keno/score")
    public String score(KenoDto.Score score, RedirectAttributes ra) {
        boolean success = kenoService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "스피드키노 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "스피드키노 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/keno/score?id=" + score.getId();
    }

    /**
     * 스피드키노 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @PostMapping(value = "zone/keno/closing")
    public AjaxResult closing() {
        return kenoService.closingAllGame();
    }

}
