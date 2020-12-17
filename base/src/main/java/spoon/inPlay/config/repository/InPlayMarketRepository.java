package spoon.inPlay.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.inPlay.config.entity.InPlayMarket;

public interface InPlayMarketRepository extends JpaRepository<InPlayMarket, Long>, QueryDslPredicateExecutor<InPlayMarket> {

    @Query("SELECT MAX(m.sort) FROM InPlayMarket  m")
    Integer getMaxSort();
}
