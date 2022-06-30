package spoon.gameZone.eos1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.gameZone.eos1.entity.Eos1;

import java.util.Date;

public interface Eos1Repository extends JpaRepository<Eos1, Long>, QueryDslPredicateExecutor<Eos1> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Eos1 o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Eos1 o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
