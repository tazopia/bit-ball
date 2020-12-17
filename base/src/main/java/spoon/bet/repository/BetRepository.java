package spoon.bet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.bet.entity.Bet;

public interface BetRepository extends JpaRepository<Bet, Long>, QueryDslPredicateExecutor<Bet> {

}
