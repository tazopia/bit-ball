package spoon.gameZone.bitcoin3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.gameZone.bitcoin3.entity.Bitcoin3;

import java.util.Date;

public interface Bitcoin3Repository extends JpaRepository<Bitcoin3, Long>, QueryDslPredicateExecutor<Bitcoin3> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Bitcoin3 o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Bitcoin3 o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
