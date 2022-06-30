package spoon.gameZone.eos4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.gameZone.eos4.entity.Eos4;

import java.util.Date;

public interface Eos4Repository extends JpaRepository<Eos4, Long>, QueryDslPredicateExecutor<Eos4> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Eos4 o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Eos4 o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
