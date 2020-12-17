package spoon.inPlay.bet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.inPlay.bet.entity.InPlayBet;

public interface InPlayBetRepository extends JpaRepository<InPlayBet, Long>, QueryDslPredicateExecutor<InPlayBet> {

}
