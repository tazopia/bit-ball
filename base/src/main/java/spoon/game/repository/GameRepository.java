package spoon.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.game.entity.Game;

import java.util.Date;

public interface GameRepository extends JpaRepository<Game, Long>, QueryDslPredicateExecutor<Game> {

    @Query(value = "SELECT MAX(g.ut) FROM Game g WHERE g.siteCode = :siteCode AND g.closing = :closing")
    String findMaxUt(@Param("siteCode") String siteCode, @Param("closing") boolean closing);

    @Query(value = "SELECT MIN(g.ut) FROM Game g WHERE g.siteCode = :siteCode")
    String minUt(@Param("siteCode") String siteCode);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Game g SET ut = :ut WHERE siteCode = :siteCode AND g.gameDate > CURRENT_TIMESTAMP")
    void reload(@Param("siteCode") String siteCode, @Param("ut") String ut);

    @Query(value = "SELECT TOP 1 groupId FROM game WHERE sports = :sports AND gameDate = :gameDate AND league = :league AND teamHome LIKE :home AND teamAway LIKE :away AND menuCode IN ('HANDICAP', 'MATCH')", nativeQuery = true)
    String findGroupId(@Param("sports") String sports, @Param("gameDate") Date gameDate,
                       @Param("league") String league, @Param("home") String home, @Param("away") String away);

    @Query(value = "SELECT TOP 1 groupId FROM game WHERE sports = :sports AND gameDate = :gameDate AND league = :league AND teamHome = :home AND teamAway = :away AND menuCode = 'SPECIAL'", nativeQuery = true)
    String findGroupIdSpecial(@Param("sports") String sports, @Param("gameDate") Date gameDate,
                               @Param("league") String league, @Param("home") String home, @Param("away") String away);
}
