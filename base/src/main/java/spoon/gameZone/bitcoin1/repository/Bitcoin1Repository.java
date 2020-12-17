package spoon.gameZone.bitcoin1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.gameZone.bitcoin1.entity.Bitcoin1;

import java.util.Date;

public interface Bitcoin1Repository extends JpaRepository<Bitcoin1, Long>, QueryDslPredicateExecutor<Bitcoin1> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Bitcoin1 o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Bitcoin1 o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
