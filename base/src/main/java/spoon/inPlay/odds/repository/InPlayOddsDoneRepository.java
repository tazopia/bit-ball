package spoon.inPlay.odds.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.inPlay.odds.entity.InPlayOddsDone;

public interface InPlayOddsDoneRepository extends JpaRepository<InPlayOddsDone, Long>, QueryDslPredicateExecutor<InPlayOddsDone> {

}
