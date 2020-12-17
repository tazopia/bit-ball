package spoon.gameZone.powerLadder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface PowerLadderRepository extends JpaRepository<PowerLadder, Long>, QueryDslPredicateExecutor<PowerLadder> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE PowerLadder o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM PowerLadder o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);

}
