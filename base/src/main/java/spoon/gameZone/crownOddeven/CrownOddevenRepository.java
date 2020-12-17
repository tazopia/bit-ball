package spoon.gameZone.crownOddeven;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface CrownOddevenRepository extends JpaRepository<CrownOddeven, Long>, QueryDslPredicateExecutor<CrownOddeven> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE CrownOddeven o SET o.odds = :odds WHERE o.gameDate > CURRENT_TIMESTAMP")
    void updateOdds(@Param("odds") double[] odds);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM CrownOddeven o WHERE o.gameDate < :gameDate")
    void deleteGame(@Param("gameDate") Date gameDate);
}
