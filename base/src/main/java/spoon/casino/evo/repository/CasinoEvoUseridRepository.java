package spoon.casino.evo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.casino.evo.entity.CasinoEvoUser;

public interface CasinoEvoUseridRepository extends JpaRepository<CasinoEvoUser, String>, QueryDslPredicateExecutor<CasinoEvoUser> {

}
