package spoon.gameZone.keno.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.gameZone.keno.entity.Keno;

import java.util.Date;

public interface KenoRepository extends JpaRepository<Keno, Long>, QueryDslPredicateExecutor<Keno> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Keno o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Keno o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);

}
