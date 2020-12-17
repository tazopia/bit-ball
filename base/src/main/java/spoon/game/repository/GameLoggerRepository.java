package spoon.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.game.entity.GameLogger;

import java.util.Date;

public interface GameLoggerRepository extends JpaRepository<GameLogger, Long>, QueryDslPredicateExecutor<GameLogger> {

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM GameLogger g WHERE g.gameId < :gameId")
    void deleteBeforeGameId(@Param("gameId") long gameId);

    @Query(value = "SELECT TOP 1 gameDate FROM Game_Logger WHERE gameId = :gameId ORDER BY id DESC", nativeQuery = true)
    Date getLastLoggerGameDate(@Param("gameId") long gameId);
}
