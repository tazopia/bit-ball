package spoon.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.customer.entity.Memo;

public interface MemoRepository extends JpaRepository<Memo, Long>, QueryDslPredicateExecutor<Memo> {

}
