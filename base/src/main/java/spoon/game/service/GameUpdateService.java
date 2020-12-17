package spoon.game.service;

import spoon.game.domain.GameDto;
import spoon.support.web.AjaxResult;

public interface GameUpdateService {

    AjaxResult update(GameDto.Update update);

    AjaxResult betEnabled(GameDto.BetEnabled betEnabled);

    AjaxResult autoUpdate(Long gameId);

    AjaxResult gameEnabled(Long[] gameIds, boolean enabled);

    AjaxResult gameDeleted(Long[] gameIds, boolean enabled);

    AjaxResult reload();

}
