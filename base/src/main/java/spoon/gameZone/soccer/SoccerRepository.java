package spoon.gameZone.soccer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface SoccerRepository extends JpaRepository<Soccer, Long>, QueryDslPredicateExecutor<Soccer> {

    @Query(value = "SELECT o.sdate FROM Soccer o WHERE o.id = :id")
    String getGameSDateById(@Param("id") Long id);

    @Query("SELECT o.sdate FROM Soccer o WHERE o.sdate = (SELECT MAX(s.sdate) FROM Soccer s WHERE s.sdate < :sdate AND s.closing = 0)")
    String getLastSdate(@Param("sdate") String sdate);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Soccer o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
