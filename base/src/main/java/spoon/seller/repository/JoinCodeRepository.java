package spoon.seller.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.seller.entity.JoinCode;

public interface JoinCodeRepository extends JpaRepository<JoinCode, Long>, QueryDslPredicateExecutor<JoinCode> {

}
