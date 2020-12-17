package spoon.gameZone.bitcoin5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.gameZone.bitcoin5.entity.Bitcoin5;

import java.util.Date;

public interface Bitcoin5Repository extends JpaRepository<Bitcoin5, Long>, QueryDslPredicateExecutor<Bitcoin5> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Bitcoin5 o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Bitcoin5 o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
