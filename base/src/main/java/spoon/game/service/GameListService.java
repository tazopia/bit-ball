package spoon.game.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.game.domain.GameDto;
import spoon.game.domain.MenuCode;
import spoon.game.entity.Game;

import java.util.List;

public interface GameListService {

    List<GameDto.List> gameList(String menu, String sports);

    List<GameDto.League> gameLeague(String menu, String sports);

    Page<Game> scoreList(MenuCode menuCode, String sports, Pageable pageable);

    Page<Game> readyGameList(GameDto.Command command, Pageable pageable);

    Page<Game> completeGameList(GameDto.Command command, Pageable pageable);

    Page<Game> closingGameList(GameDto.Command command, Pageable pageable);

    Page<Game> resultGameList(GameDto.Command command, Pageable pageable);

    Page<Game> deletedGameList(GameDto.Command command, Pageable pageable);
}
