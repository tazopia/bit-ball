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
import spoon.gameZone.bitcoin3.domain.Bitcoin3Config;
import spoon.gameZone.bitcoin3.domain.Bitcoin3Dto;
import spoon.gameZone.bitcoin3.service.Bitcoin3Service;
import spoon.support.web.AjaxResult;

@Slf4j
@RequiredArgsConstructor
@Controller("admin.bitcoin3Controller")
@RequestMapping(value = "#{config.pathAdmin}")
public class Bitcoin3Controller {

    private final Bitcoin3Service bitcoin3Service;

    /**
     * 비트코인 3분 설정
     */
    @RequestMapping(value = "zone/bitcoin3/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getBitcoin3());
        return "admin/zone/bitcoin3/config";
    }

    /**
     * 비트코인 3분 설정 변경
     */
    @RequestMapping(value = "zone/bitcoin3/config", method = RequestMethod.POST)
    public String config(Bitcoin3Config bitcoin3Config, RedirectAttributes ra) {
        boolean success = bitcoin3Service.updateConfig(bitcoin3Config);
        if (success) {
            ra.addFlashAttribute("message", "비트코인 3분 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "비트코인 3분 설정변경에 실패하였습니다.");
        }

        return "redirect:" + Config.getPathAdmin() + "/zone/bitcoin3/config";
    }

    /**
     * 비트코인 3분 진행
     */
    @RequestMapping(value = "zone/bitcoin3/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", bitcoin3Service.getComplete());
        map.addAttribute("config", ZoneConfig.getBitcoin3());
        return "admin/zone/bitcoin3/complete";
    }

    /**
     * 비트코인 3분 완료
     */
    @RequestMapping(value = "zone/bitcoin3/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", bitcoin3Service.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getBitcoin3());
        return "admin/zone/bitcoin3/closing";
    }

    /**
     * 비트코인 3분 스코어 입력 폼
     */
    @RequestMapping(value = "zone/bitcoin3/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", bitcoin3Service.findScore(id));
        return "admin/zone/bitcoin3/score";
    }

    /**
     * 비트코인 3분 결과처리
     */
    @RequestMapping(value = "zone/bitcoin3/score", method = RequestMethod.POST)
    public String score(Bitcoin3Dto.Score score, RedirectAttributes ra) {
        boolean success = bitcoin3Service.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "비트코인 3분 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "비트코인 3분 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/bitcoin3/score?id=" + score.getId();
    }

    /**
     * 비트코인 3분 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/bitcoin3/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return bitcoin3Service.closingAllGame();
    }

}
