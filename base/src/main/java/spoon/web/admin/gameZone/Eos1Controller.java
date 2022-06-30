package spoon.web.admin.gameZone;

import lombok.RequiredArgsConstructor;
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
import spoon.gameZone.eos1.domain.Eos1Config;
import spoon.gameZone.eos1.domain.Eos1Dto;
import spoon.gameZone.eos1.service.Eos1Service;
import spoon.support.web.AjaxResult;

@Slf4j
@RequiredArgsConstructor
@Controller("admin.eos1Controller")
@RequestMapping(value = "#{config.pathAdmin}")
public class Eos1Controller {

    private final Eos1Service eos1Service;

    /**
     * 파워볼 설정
     */
    @RequestMapping(value = "zone/eos1/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getEos1());
        return "admin/zone/eos1/config";
    }

    /**
     * 파워볼 설정 변경
     */
    @RequestMapping(value = "zone/eos1/config", method = RequestMethod.POST)
    public String config(Eos1Config eos1Config, RedirectAttributes ra) {
        boolean success = eos1Service.updateConfig(eos1Config);
        if (success) {
            ra.addFlashAttribute("message", "EOS 1분 파워볼 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "EOS 1분 파워볼 설정변경에 실패하였습니다.");
        }

        return "redirect:" + Config.getPathAdmin() + "/zone/eos1/config";
    }

    /**
     * 파워볼 진행
     */
    @RequestMapping(value = "zone/eos1/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", eos1Service.getComplete());
        map.addAttribute("config", ZoneConfig.getEos1());
        return "admin/zone/eos1/complete";
    }

    /**
     * 파워볼 완료
     */
    @RequestMapping(value = "zone/eos1/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", eos1Service.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getEos1());
        return "admin/zone/eos1/closing";
    }

    /**
     * 파워볼 스코어 입력 폼
     */
    @RequestMapping(value = "zone/eos1/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", eos1Service.findScore(id));
        return "admin/zone/eos1/score";
    }

    /**
     * 파워볼 결과처리
     */
    @RequestMapping(value = "zone/eos1/score", method = RequestMethod.POST)
    public String score(Eos1Dto.Score score, RedirectAttributes ra) {
        boolean success = eos1Service.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "EOS 1분 파워볼 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "EOS 1분 파워볼 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/eos1/score?id=" + score.getId();
    }

    /**
     * 파워볼 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/eos1/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return eos1Service.closingAllGame();
    }

}
