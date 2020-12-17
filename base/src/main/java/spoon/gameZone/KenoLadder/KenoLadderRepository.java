package spoon.gameZone.KenoLadder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface KenoLadderRepository extends JpaRepository<KenoLadder, Long>, QueryDslPredicateExecutor<KenoLadder> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE KenoLadder o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM KenoLadder o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);

}
