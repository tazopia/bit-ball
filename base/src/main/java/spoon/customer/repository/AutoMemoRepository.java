package spoon.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.customer.entity.AutoMemo;

public interface AutoMemoRepository extends JpaRepository<AutoMemo, Long>, QueryDslPredicateExecutor<AutoMemo> {

}
