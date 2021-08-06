package spoon.casino.evolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.casino.evolution.entity.CasinoEvolutionId;

public interface CasinoEvolutionIdRepository extends JpaRepository<CasinoEvolutionId, String>, QueryDslPredicateExecutor<CasinoEvolutionId> {

}
