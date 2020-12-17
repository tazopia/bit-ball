package spoon.inPlay.odds.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.inPlay.odds.entity.InPlayScore;

public interface InPlayScoreRepository extends JpaRepository<InPlayScore, Long>, QueryDslPredicateExecutor<InPlayScore> {

}
