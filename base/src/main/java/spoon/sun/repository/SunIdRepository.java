package spoon.sun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.sun.entity.SunId;

public interface SunIdRepository extends JpaRepository<SunId, String>, QueryDslPredicateExecutor<SunId> {

}
