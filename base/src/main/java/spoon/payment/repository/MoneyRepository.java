package spoon.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.payment.entity.Money;

public interface MoneyRepository extends JpaRepository<Money, Long>, QueryDslPredicateExecutor<Money> {

}
