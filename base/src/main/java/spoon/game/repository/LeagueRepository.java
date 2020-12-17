package spoon.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.game.entity.League;

public interface LeagueRepository extends JpaRepository<League, Long>, QueryDslPredicateExecutor<League> {

}
