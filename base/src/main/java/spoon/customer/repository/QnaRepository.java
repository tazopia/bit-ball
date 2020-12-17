package spoon.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.customer.entity.Qna;

public interface QnaRepository extends JpaRepository<Qna, Long>, QueryDslPredicateExecutor<Qna> {

}
