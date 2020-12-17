package spoon.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.member.entity.LoginHistory;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long>, QueryDslPredicateExecutor<LoginHistory> {

}
