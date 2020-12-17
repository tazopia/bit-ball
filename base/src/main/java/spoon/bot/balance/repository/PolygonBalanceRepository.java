package spoon.bot.balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.bot.balance.entity.PolygonBalance;

public interface PolygonBalanceRepository extends JpaRepository<PolygonBalance, Long>, QueryDslPredicateExecutor<PolygonBalance> {
    
}
