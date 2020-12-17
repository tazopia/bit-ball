package spoon.inPlay.odds.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.inPlay.odds.entity.InPlayGame;

public interface InPlayGameRepository extends JpaRepository<InPlayGame, Long>, QueryDslPredicateExecutor<InPlayGame> {

}
