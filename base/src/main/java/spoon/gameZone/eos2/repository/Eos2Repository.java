package spoon.gameZone.eos2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.gameZone.eos2.entity.Eos2;

import java.util.Date;

public interface Eos2Repository extends JpaRepository<Eos2, Long>, QueryDslPredicateExecutor<Eos2> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Eos2 o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Eos2 o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
