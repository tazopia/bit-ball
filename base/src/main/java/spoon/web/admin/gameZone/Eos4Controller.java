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
import spoon.gameZone.eos4.domain.Eos4Config;
import spoon.gameZone.eos4.domain.Eos4Dto;
import spoon.gameZone.eos4.service.Eos4Service;
import spoon.support.web.AjaxResult;

@Slf4j
@RequiredArgsConstructor
@Controller("admin.eos4Controller")
@RequestMapping(value = "#{config.pathAdmin}")
public class Eos4Controller {

    private final Eos4Service eos4Service;

    /**
     * 파워볼 설정
     */
    @RequestMapping(value = "zone/eos4/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getEos4());
        return "admin/zone/eos4/config";
    }

    /**
     * 파워볼 설정 변경
     */
    @RequestMapping(value = "zone/eos4/config", method = RequestMethod.POST)
    public String config(Eos4Config eos4Config, RedirectAttributes ra) {
        boolean success = eos4Service.updateConfig(eos4Config);
        if (success) {
            ra.addFlashAttribute("message", "EOS 4분 파워볼 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "EOS 4분 파워볼 설정변경에 실패하였습니다.");
        }

        return "redirect:" + Config.getPathAdmin() + "/zone/eos4/config";
    }

    /**
     * 파워볼 진행
     */
    @RequestMapping(value = "zone/eos4/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", eos4Service.getComplete());
        map.addAttribute("config", ZoneConfig.getEos4());
        return "admin/zone/eos4/complete";
    }

    /**
     * 파워볼 완료
     */
    @RequestMapping(value = "zone/eos4/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", eos4Service.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getEos4());
        return "admin/zone/eos4/closing";
    }

    /**
     * 파워볼 스코어 입력 폼
     */
    @RequestMapping(value = "zone/eos4/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", eos4Service.findScore(id));
        return "admin/zone/eos4/score";
    }

    /**
     * 파워볼 결과처리
     */
    @RequestMapping(value = "zone/eos4/score", method = RequestMethod.POST)
    public String score(Eos4Dto.Score score, RedirectAttributes ra) {
        boolean success = eos4Service.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "EOS 4분 파워볼 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "EOS 4분 파워볼 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/eos4/score?id=" + score.getId();
    }

    /**
     * 파워볼 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/eos4/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return eos4Service.closingAllGame();
    }

}
