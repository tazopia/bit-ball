package spoon.inPlay.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.inPlay.config.entity.InPlayLeague;

public interface InPlayLeagueRepository extends JpaRepository<InPlayLeague, String>, QueryDslPredicateExecutor<InPlayLeague> {

}
