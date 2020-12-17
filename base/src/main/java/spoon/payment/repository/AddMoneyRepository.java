package spoon.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.payment.entity.AddMoney;

public interface AddMoneyRepository extends JpaRepository<AddMoney, Long>, QueryDslPredicateExecutor<AddMoney> {


}
