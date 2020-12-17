package spoon.web.admin.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.config.domain.GameConfig;
import spoon.config.service.ConfigService;
import spoon.game.service.sports.SportsService;

@AllArgsConstructor
@Controller("admin.gameConfigController")
@RequestMapping("#{config.pathAdmin}")
public class GameConfigController {

    private ConfigService configService;

    private SportsService sportsService;

    /**
     * 환경설정 - 게임 환경설정
     */
    @RequestMapping(value = "config/game", method = RequestMethod.GET)
    public String gameConfig(ModelMap map) {
        map.addAttribute("sportsList", sportsService.getAll());
        return "admin/config/game";
    }

    /**
     * 환경설정 - 게임 환경설정 - 수정프로세스
     */
    @RequestMapping(value = "config/game", method = RequestMethod.POST)
    public String gameConfig(GameConfig gameConfig, RedirectAttributes ra) {
        configService.updateGameConfig(gameConfig);
        ra.addFlashAttribute("message", "게임 환경설정을 업데이트 하였습니다.");
        return "redirect:" + Config.getPathAdmin() + "/config/game";
    }

}
