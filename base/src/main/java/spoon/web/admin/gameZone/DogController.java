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
import spoon.gameZone.dog.DogConfig;
import spoon.gameZone.dog.DogDto;
import spoon.gameZone.dog.service.DogService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.dogController")
@RequestMapping(value = "#{config.pathAdmin}")
public class DogController {

    private DogService dogService;

    /**
     * 개경주 설정
     */
    @RequestMapping(value = "zone/dog/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getDog());
        return "admin/zone/dog/config";
    }

    /**
     * 개경주 설정 변경
     */
    @RequestMapping(value = "zone/dog/config", method = RequestMethod.POST)
    public String config(DogConfig dogConfig, RedirectAttributes ra) {
        boolean success = dogService.updateConfig(dogConfig);
        if (success) {
            ra.addFlashAttribute("message", "개경주 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "개경주 설정변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/zone/dog/config";
    }

    /**
     * 개경주 진행
     */
    @RequestMapping(value = "zone/dog/complete", method = RequestMethod.GET)
    public String complete(ModelMap map) {
        map.addAttribute("list", dogService.getComplete());
        map.addAttribute("config", ZoneConfig.getDog());
        return "admin/zone/dog/complete";
    }

    /**
     * 개경주 완료
     */
    @RequestMapping(value = "zone/dog/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @ModelAttribute("command") ZoneDto.Command command,
                          @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "gameDate") Pageable pageable) {
        map.addAttribute("page", dogService.getClosing(command, pageable));
        map.addAttribute("config", ZoneConfig.getDog());
        return "admin/zone/dog/closing";
    }

    /**
     * 개경주 스코어 입력 폼
     */
    @RequestMapping(value = "zone/dog/score", method = RequestMethod.GET)
    public String score(ModelMap map, Long id) {
        map.addAttribute("score", dogService.findScore(id));
        return "admin/zone/dog/score";
    }

    /**
     * 개경주 결과처리
     */
    @RequestMapping(value = "zone/dog/score", method = RequestMethod.POST)
    public String score(DogDto.Score score, RedirectAttributes ra) {
        boolean success = dogService.closingGame(score);

        if (success) {
            ra.addFlashAttribute("message", "개경주 결과처리를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "개경주 결과처리에 실패 하였습니다.");
        }
        ra.addFlashAttribute("popup", "closing");

        return "redirect:" + Config.getPathAdmin() + "/zone/dog/score?id=" + score.getId();
    }

    /**
     * 개경주 베팅 없는경기 모두 결과처리
     */
    @ResponseBody
    @RequestMapping(value = "zone/dog/closing", method = RequestMethod.POST)
    public AjaxResult closing() {
        return dogService.closingAllGame();
    }
}
