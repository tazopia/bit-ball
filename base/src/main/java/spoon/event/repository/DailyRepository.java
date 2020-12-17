package spoon.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.event.entity.Daily;

public interface DailyRepository extends JpaRepository<Daily, Long>, QueryDslPredicateExecutor<Daily> {

}
