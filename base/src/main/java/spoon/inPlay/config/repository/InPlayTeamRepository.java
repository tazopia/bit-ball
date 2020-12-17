package spoon.inPlay.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.inPlay.config.entity.InPlayTeam;

public interface InPlayTeamRepository extends JpaRepository<InPlayTeam, String>, QueryDslPredicateExecutor<InPlayTeam> {

}
