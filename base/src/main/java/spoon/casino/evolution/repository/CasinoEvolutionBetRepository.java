package spoon.casino.evolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.casino.evolution.entity.CasinoEvolutionBet;

public interface CasinoEvolutionBetRepository extends JpaRepository<CasinoEvolutionBet, String>, QueryDslPredicateExecutor<CasinoEvolutionBet> {

}
