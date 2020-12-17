package spoon.inPlay.odds.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.inPlay.odds.entity.InPlayOdds;

import java.util.List;

public interface InPlayOddsRepository extends JpaRepository<InPlayOdds, Long>, QueryDslPredicateExecutor<InPlayOdds> {

    @Query(value = "SELECT * FROM INPLAY_ODDS o LEFT JOIN INPLAY_GAME g ON o.fixtureId = g.fixtureId WHERE o.settlement = 0 AND g.status > 2 AND o.lastUpdate < :lastUpdate", nativeQuery = true)
    List<InPlayOdds> find5HourBefore(@Param("lastUpdate") long lastUpdate);

}
