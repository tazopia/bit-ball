package spoon.web.admin.bet;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.bet.domain.BetDto;
import spoon.bet.service.BetListService;
import spoon.bet.service.BetService;
import spoon.game.domain.MenuCode;
import spoon.game.service.GameService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller("admin.betListController")
@RequestMapping("#{config.pathAdmin}")
public class BetListController {

    private BetService betService;

    private BetListService betListService;

    private GameService gameService;

    /**
     * 베팅 리스트
     */
    @RequestMapping(value = "/betting/list", method = RequestMethod.GET)
    public String list(ModelMap map, BetDto.Command command,
                       @PageableDefault(size = 20) Pageable pageable) {
        map.addAttribute("page", betListService.adminPage(command, pageable));
        map.addAttribute("command", command);
        map.addAttribute("zone", MenuCode.getZoneList());
        return "admin/betting/list";
    }

    /**
     * 베팅 리스트 유저별 팝업
     */
    @RequestMapping(value = "/betting/popup/{userid}", method = RequestMethod.GET)
    public String popup(ModelMap map, @PathVariable("userid") String userid, BetDto.Command command,
                        @PageableDefault(size = 20) Pageable pageable) {
        command.setUserid(userid);
        map.addAttribute("page", betListService.adminPage(command, pageable));
        map.addAttribute("command", command);
        map.addAttribute("zone", MenuCode.getZoneList());
        return "admin/betting/popup/user";
    }

    /**
     * 베팅 리스트 게시판 입력용
     */
    @RequestMapping(value = "/betting/popup/board", method = RequestMethod.GET)
    public String popup(ModelMap map, BetDto.Command command,
                        @PageableDefault(size = 20) Pageable pageable) {
        map.addAttribute("page", betListService.adminPage(command, pageable));
        map.addAttribute("command", command);
        return "admin/betting/popup/board";
    }

    /**
     * 베팅 리스트 게임별 팝업
     */
    @RequestMapping(value = "/betting/{betTeam:home|draw|away}/{gameId}", method = RequestMethod.GET)
    public String popup(ModelMap map, @PathVariable("gameId") Long gameId, @PathVariable("betTeam") String betTeam) {
        map.addAttribute("list", betListService.listByGame(gameId, betTeam));
        map.addAttribute("game", gameService.getGame(gameId));
        return "admin/betting/popup/game";
    }

    /**
     * 게임존 게임별 팝업
     */
    @RequestMapping(value = "/betting/zone/{gameCode}/{sdate}", method = RequestMethod.GET)
    public String popup(ModelMap map, @PathVariable("gameCode") String gameCode, @PathVariable("sdate") String sdate) {
        MenuCode menuCode = MenuCode.valueOf(gameCode.toUpperCase());
        map.addAttribute("list", betListService.listByGame(menuCode, sdate));
        return "admin/betting/popup/zone";
    }

    /**
     * 관리자 베팅 취소
     */
    @ResponseBody
    @RequestMapping(value = "/betting/cancel/item", method = RequestMethod.POST)
    public AjaxResult cancel(Long betId, Long itemId) {
        return betService.cancelItem(betId, itemId);
    }

    /**
     * 관리자 베팅 전체 취소
     */
    @ResponseBody
    @RequestMapping(value = "/betting/cancel/bet", method = RequestMethod.POST)
    public AjaxResult cancel(Long betId) {
        return betService.cancelBet(betId);
    }

    @ResponseBody
    @RequestMapping(value = "/betting/black", method = RequestMethod.POST)
    public AjaxResult black(Long betId) {
        return betService.removeBlack(betId);
    }
}
