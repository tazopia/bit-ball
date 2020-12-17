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
import spoon.gameZone.bitcoin1.domain.Bitcoin1Config;
import spoon.gameZone.bitcoin1.domain.Bitcoin1Dto;
import spoon.gameZone.bitcoin1.service.Bitcoin1Service;
import spoon.support.web.AjaxResult;

@Slf4j
@RequiredArgsConstructor
@Controller("admin.bitcoin1Controller")
@RequestMapping(value = "#{config.pathAdmin}")
public class Bitcoin1Controller {

    private final Bitcoin1Service bitcoin1Service;

    /**
     * 비트코인 1분 설정
     */
    @RequestMapping(value = "zone/bitcoin1/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getBitcoin1());
        return "admin/zone/bitcoin1/config";
    }

    /**
     * 비트코인 1분 설정 변경
     */
    @RequestMapping(value = "zone/bitcoin1/config", method = RequestMethod.POST)
    public String config(Bitcoin1Config bitcoin1Config, RedirectAttributes ra) {
        boolean success = bitcoin1Service.updateConfig(bitcoin1Config);
        if (success) {
            ra.addFlashAttribute("message", "비트코인 1분 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "비트코인 1분 설정변경에 실패하였습니다.");
        }

        return "redirect:" + Config.getPathAdmin() + "/zone/bitcoin1/config";
    }

    /**
     * 비트코인 1분 진행
     */
    @RequestMapping(value = "zone/bitcoin1/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", bitcoin1Service.getComplete());
        map.addAttribute("config", ZoneConfig.getBitcoin1());
        return "admin/zone/bitcoin1/complete";
    }

    /**
     * 비트코인 1분 완료
     */
    @RequestMapping(value = "zone/bitcoin1/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", bitcoin1Service.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getBitcoin1());
        return "admin/zone/bitcoin1/closing";
    }

    /**
     * 비트코인 1분 스코어 입력 폼
     */
    @RequestMapping(value = "zone/bitcoin1/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", bitcoin1Service.findScore(id));
        return "admin/zone/bitcoin1/score";
    }

    /**
     * 비트코인 1분 결과처리
     */
    @RequestMapping(value = "zone/bitcoin1/score", method = RequestMethod.POST)
    public String score(Bitcoin1Dto.Score score, RedirectAttributes ra) {
        boolean success = bitcoin1Service.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "비트코인 1분 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "비트코인 1분 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/bitcoin1/score?id=" + score.getId();
    }

    /**
     * 비트코인 1분 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/bitcoin1/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return bitcoin1Service.closingAllGame();
    }

}
