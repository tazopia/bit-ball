package spoon.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.event.entity.DailyPayment;

public interface DailyPaymentRepository extends JpaRepository<DailyPayment, Long>, QueryDslPredicateExecutor<DailyPayment> {

}
