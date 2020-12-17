package spoon.game.service;

import spoon.game.domain.GameDto;
import spoon.game.entity.Game;
import spoon.support.web.AjaxResult;

public interface GameService {

    AjaxResult gameScore(GameDto.Result result);

    AjaxResult gameReset(Long id);

    Game getGame(Long gameId);

    void addGame(Game game);

    String getGroupId(Game game);
}
