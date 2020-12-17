package spoon.web.site.game;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spoon.bet.domain.BetDto;
import spoon.bet.service.BetGameService;
import spoon.game.domain.MenuCode;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@RestController
@RequestMapping("#{config.pathSite}")
public class GameBetController {

    private BetGameService betGameService;

    /**
     * 베팅하기
     */
    @RequestMapping(value = "game/{menu:cross|special|live}/betting", method = RequestMethod.POST)
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @PathVariable("menu") String menu,
                              @RequestBody BetDto.BetGame betGame) {

        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }

        if (betGame.getIds() == null || betGame.getBets() == null || betGame.getIds().length == 0 || betGame.getBets().length == 0) {
            return new AjaxResult(false, "선택된 경기가 없습니다.");
        }

        if (betGame.getIds().length != betGame.getBets().length) {
            return new AjaxResult(false, "베팅 정보가 정확하지 않습니다.\n\n다시 확인하여 주세요.");
        }

        betGame.setMenuCode(MenuCode.valueOf(menu.toUpperCase()));
        return betGameService.addGameBetting(betGame);
    }

}
