package spoon.web.admin.game;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spoon.game.domain.GameDto;
import spoon.game.service.GameService;
import spoon.game.service.GameUpdateService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@RestController("admin.gameUpdateController")
@RequestMapping(value = "#{config.pathAdmin}")
public class GameUpdateController {

    private GameUpdateService gameUpdateService;

    private GameService gameService;

    @RequestMapping(value = "game/update", method = RequestMethod.POST)
    public AjaxResult update(GameDto.Update update) {
        return gameUpdateService.update(update);
    }

    /**
     * 스포츠 > 리스트 > 베팅 가능 불가능
     */
    @RequestMapping(value = "game/betEnabled", method = RequestMethod.POST)
    public AjaxResult betEnabled(GameDto.BetEnabled betEnabled) {
        return gameUpdateService.betEnabled(betEnabled);
    }

    /**
     * 스포츠 > 리스트 > 배당 자동업데이트 수동 업데이트
     */
    @RequestMapping(value = "game/autoUpdate", method = RequestMethod.POST)
    public AjaxResult autoUpdate(Long gameId) {
        return gameUpdateService.autoUpdate(gameId);
    }

    /**
     * 스포츠 > 리스트 > 선택항목 등록 / 등록취소
     */
    @RequestMapping(value = "game/enabled", method = RequestMethod.POST)
    public AjaxResult gameEnabled(Long[] gameIds, boolean enabled) {
        return gameUpdateService.gameEnabled(gameIds, enabled);
    }

    /**
     * 스포츠 > 리스트 > 선택항목 등록 / 삭제
     */
    @RequestMapping(value = "game/deleted", method = RequestMethod.POST)
    public AjaxResult gameDeleted(Long[] gameIds, boolean enabled) {
        return gameUpdateService.gameDeleted(gameIds, enabled);
    }

    /**
     * 스포츠 > 리스트 > 리로드 배당
     */
    @RequestMapping(value = "game/reload", method = RequestMethod.POST)
    public AjaxResult gameReload() {
        return gameUpdateService.reload();
    }

    /**
     * 스포츠 > 스코어 입력
     */
    @RequestMapping(value = "game/score", method = RequestMethod.POST)
    public AjaxResult gameScore(GameDto.Result result) {
        return gameService.gameScore(result);
    }

    /**
     * 종료 경기 리셋하기
     */
    @RequestMapping(value = "game/reset", method = RequestMethod.POST)
    public AjaxResult gameReset(Long id) {
        return gameService.gameReset(id);
    }
}
