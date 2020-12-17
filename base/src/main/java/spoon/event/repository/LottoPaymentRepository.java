package spoon.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.event.entity.LottoPayment;

public interface LottoPaymentRepository extends JpaRepository<LottoPayment, Long>, QueryDslPredicateExecutor<LottoPayment> {

}
