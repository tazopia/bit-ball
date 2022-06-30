package spoon.gameZone.eos3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.gameZone.eos3.entity.Eos3;

import java.util.Date;

public interface Eos3Repository extends JpaRepository<Eos3, Long>, QueryDslPredicateExecutor<Eos3> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Eos3 o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Eos3 o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
