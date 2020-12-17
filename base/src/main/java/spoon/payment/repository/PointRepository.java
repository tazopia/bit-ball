package spoon.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.payment.entity.Point;

public interface PointRepository extends JpaRepository<Point, Long>, QueryDslPredicateExecutor<Point> {

}
