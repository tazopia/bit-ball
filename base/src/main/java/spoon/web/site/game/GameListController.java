package spoon.web.site.game;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.board.service.NoticeService;
import spoon.common.utils.JsonUtils;
import spoon.config.service.GameConfigService;
import spoon.game.domain.GameDto;
import spoon.game.domain.MenuCode;
import spoon.game.service.GameListService;
import spoon.member.domain.UserBetInfo;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class GameListController {

    private GameListService gameListService;

    private GameConfigService gameConfigService;

    private NoticeService noticeService;

    /**
     * 스포츠 사용자 List
     */
    @RequestMapping(value = "game/{menu:cross|special|live}", method = RequestMethod.GET)
    public String list(ModelMap map, @PathVariable("menu") String menu, String sports) {
        List<GameDto.List> lists = gameListService.gameList(menu, sports);
        List<GameDto.League> leagues = gameListService.gameLeague(menu, sports);
        long total = leagues.stream().mapToLong(GameDto.League::getCnt).sum();

        MenuCode menuCode = MenuCode.valueOf(menu.toUpperCase());
        map.addAttribute("config", JsonUtils.toString(gameConfigService.gameConfig(menuCode)));
        map.addAttribute("gameCnt", total);
        map.addAttribute("gameList", JsonUtils.toString(lists));
        map.addAttribute("league", JsonUtils.toString(leagues));
        map.addAttribute("sports", sports);
        map.addAttribute("menu", menu);
        map.addAttribute("notice", noticeService.siteList());
        map.addAttribute("title", menuCode.getName());
        map.addAttribute("betInfo", new UserBetInfo());

        return "site/game/list";
    }

    /**
     * 스포츠 사용자 List Ajax
     */
    @ResponseBody
    @RequestMapping(value = "game/{menu:cross|special|live}", method = RequestMethod.POST)
    public List<GameDto.List> lists(@PathVariable("menu") String menu, String sports) {
        return gameListService.gameList(menu, sports);
    }
}
