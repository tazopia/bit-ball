package spoon.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.payment.entity.AddPoint;

public interface AddPointRepository extends JpaRepository<AddPoint, Long>, QueryDslPredicateExecutor<AddPoint> {

}
