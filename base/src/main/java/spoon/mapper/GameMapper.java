package spoon.mapper;

import org.apache.ibatis.annotations.Param;
import spoon.game.domain.GameDto;
import spoon.game.domain.LeagueDto;

import java.util.Date;
import java.util.List;

public interface GameMapper {

    List<GameDto.Main> mainList();

    List<LeagueDto.List> readyLeague(String menu);

    List<LeagueDto.List> completeLeague(String menu);

    List<LeagueDto.List> resultLeague(String menu);

    List<LeagueDto.List> closingLeague(String menu);

    List<GameDto.List> gameList(@Param("menu") String menu, @Param("sports") String sports, @Param("spread") int spread);

    List<GameDto.List> gameEndList(@Param("menu") String menu, @Param("sports") String sports, @Param("spread") int spread);

    List<GameDto.League> gameLeague(@Param("menu") String menu, @Param("sports") String sports, @Param("spread") int spread);

    String findGroupId(@Param("siteCode") String siteCode, @Param("gameDate") Date gameDate, @Param("league") String league, @Param("teamHome") String teamHome, @Param("teamAway") String teamAway);

    void deleteBeforeGameDate(@Param("gameDate") Date gameDate);

    List<GameDto.Bot> initOdds(@Param("siteCode") String siteCode);

    List<GameDto.Bot> initScore(@Param("siteCode") String siteCode);
}
