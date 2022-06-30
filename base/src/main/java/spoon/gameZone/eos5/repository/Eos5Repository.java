package spoon.gameZone.eos5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.gameZone.eos5.entity.Eos5;

import java.util.Date;

public interface Eos5Repository extends JpaRepository<Eos5, Long>, QueryDslPredicateExecutor<Eos5> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Eos5 o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Eos5 o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
