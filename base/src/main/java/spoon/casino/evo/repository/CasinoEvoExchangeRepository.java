package spoon.casino.evo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.casino.evo.entity.CasinoEvoExchange;

public interface CasinoEvoExchangeRepository extends JpaRepository<CasinoEvoExchange, Long>, QueryDslPredicateExecutor<CasinoEvoExchange> {

}
