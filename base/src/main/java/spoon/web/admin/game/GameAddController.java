package spoon.web.admin.game;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.game.service.GameAddService;

@Slf4j
@AllArgsConstructor
@Controller("admin.gameAddController")
@RequestMapping("#{config.pathAdmin}")
public class GameAddController {

    private GameAddService gameAddService;

    /**
     * 게임 등록 폼
     */
    @RequestMapping(value = "game/popup/add", method = RequestMethod.GET)
    public String gameAdd() {
        return "admin/game/popup/add";
    }

    /**
     * 게임 등록 프로세스
     */
    @RequestMapping(value = "game/popup/add", method = RequestMethod.POST)
    public String gameAdd(@RequestParam("gameText") String gameText, RedirectAttributes ra) {
        long count = gameAddService.addGame(gameText);
        ra.addFlashAttribute("message", count + " 게임을 등록하였습니다.");
        return "redirect:" + Config.getPathAdmin() + "/game/popup/add";
    }

}
