package spoon.web.admin.game;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.game.domain.GameDto;
import spoon.game.domain.MenuCode;
import spoon.game.entity.Sports;
import spoon.game.service.GameListService;
import spoon.game.service.GameLoggerService;
import spoon.game.service.sports.SportsService;
import spoon.mapper.GameMapper;


@Slf4j
@AllArgsConstructor
@Controller("admin.gameListController")
@RequestMapping(value = "#{config.pathAdmin}")
public class GameListController {

    private GameListService gameListService;

    private GameLoggerService gameLoggerService;

    private SportsService sportsService;

    private GameMapper gameMapper;


    @ModelAttribute("sportsList")
    public Iterable<Sports> sportsList() {
        return sportsService.getAll();
    }

    /**
     * 스포츠 등록대기
     */
    @RequestMapping(value = "game/{menu:match|handicap|cross|special|live}/ready", method = RequestMethod.GET)
    public String ready(ModelMap map, @PathVariable("menu") String menu, GameDto.Command command,
                        @PageableDefault(size = 50) Pageable pageable) {
        command.setMenu("ready");
        command.setMenuCode(MenuCode.valueOf(menu.toUpperCase()));

        map.addAttribute("page", gameListService.readyGameList(command, pageable));
        map.addAttribute("leagueList", gameMapper.readyLeague(menu));
        map.addAttribute("icon", getCssClass(menu));
        map.addAttribute("config", JsonUtils.toString(new GameDto.GameConfig()));

        return "admin/game/ready";
    }

    /**
     * 스포츠 등록완료
     */
    @RequestMapping(value = "game/{menu:match|handicap|cross|special|live}/complete", method = RequestMethod.GET)
    public String complete(ModelMap map, @PathVariable("menu") String menu, GameDto.Command command,
                           @PageableDefault(size = 50) Pageable pageable) {
        command.setMenu("complete");
        command.setMenuCode(MenuCode.valueOf(menu.toUpperCase()));

        map.addAttribute("page", gameListService.completeGameList(command, pageable));
        map.addAttribute("leagueList", gameMapper.completeLeague(menu));
        map.addAttribute("icon", getCssClass(menu));
        map.addAttribute("config", JsonUtils.toString(new GameDto.GameConfig()));

        return "admin/game/complete";
    }

    /**
     * 스포츠 게임종료
     */
    @RequestMapping(value = "game/{menu:match|handicap|cross|special|live}/closing", method = RequestMethod.GET)
    public String closing(ModelMap map, @PathVariable("menu") String menu, GameDto.Command command,
                          @PageableDefault(size = 50) Pageable pageable) {
        command.setMenu("closing");
        command.setMenuCode(MenuCode.valueOf(menu.toUpperCase()));

        map.addAttribute("page", gameListService.closingGameList(command, pageable));
        map.addAttribute("leagueList", gameMapper.closingLeague(menu));
        map.addAttribute("icon", getCssClass(menu));
        map.addAttribute("config", JsonUtils.toString(new GameDto.GameConfig()));

        return "admin/game/closing";
    }

    /**
     * 스포츠 결과처리
     */
    @RequestMapping(value = "game/{menu:cross|special|live}/result", method = RequestMethod.GET)
    public String result(ModelMap map, @PathVariable("menu") String menu, GameDto.Command command,
                         @PageableDefault(size = 50) Pageable pageable) {
        command.setMenu("result");
        command.setMenuCode(MenuCode.valueOf(menu.toUpperCase()));

        map.addAttribute("page", gameListService.resultGameList(command, pageable));
        map.addAttribute("leagueList", gameMapper.resultLeague(menu));
        map.addAttribute("icon", getCssClass(command.getMenu()));
        map.addAttribute("config", JsonUtils.toString(new GameDto.GameConfig()));

        return "admin/game/result";
    }

    /**
     * 스포츠 삭제된 경기
     */
    @RequestMapping(value = "game/deleted", method = RequestMethod.GET)
    public String deleted(ModelMap map, GameDto.Command command,
                          @PageableDefault(size = 50) Pageable pageable) {
        if (StringUtils.empty(command.getMenu())) {
            command.setMenu("");
            command.setMenuCode(MenuCode.NONE);
        } else {
            command.setMenuCode(MenuCode.valueOf(command.getMenu().toUpperCase()));
        }

        map.addAttribute("page", gameListService.deletedGameList(command, pageable));
        map.addAttribute("icon", getCssClass(command.getMenu()));
        map.addAttribute("config", JsonUtils.toString(new GameDto.GameConfig()));

        return "admin/game/deleted";
    }

    /**
     * 스포츠 업데이트 로그
     */
    @RequestMapping(value = "game/logger/{gameId}", method = RequestMethod.GET)
    public String gameLogger(ModelMap map, @PathVariable("gameId") long gameId) {
        map.addAttribute("list", gameLoggerService.getGameLogger(gameId));
        return "admin/game/popup/logger";
    }

    private String getCssClass(String menu) {
        switch (menu) {
            case "match":
            case "cross":
                return "bul fa fa-futbol-o";
            case "handicap":
                return "bul fa fa-hand-paper-o";
            case "special":
                return "bul fa fa-hashtag";
            case "live":
                return "bul fa fa-clock-o";
            default:
                return "bul fa fa-minus-circle";
        }
    }
}
