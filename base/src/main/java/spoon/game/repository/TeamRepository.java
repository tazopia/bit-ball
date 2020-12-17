package spoon.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.game.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long>, QueryDslPredicateExecutor<Team> {

}
