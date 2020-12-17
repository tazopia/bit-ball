package spoon.gameZone.dog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface DogRepository extends JpaRepository<Dog, Long>, QueryDslPredicateExecutor<Dog> {

    @Query(value = "SELECT o.sdate FROM Dog o WHERE o.id = :id")
    String getGameSDateById(@Param("id") Long id);

    @Query("SELECT o.sdate FROM Dog o WHERE o.sdate = (SELECT MAX(s.sdate) FROM Dog s WHERE s.sdate < :sdate AND s.closing = 0)")
    String getLastSdate(@Param("sdate") String sdate);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Dog o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
