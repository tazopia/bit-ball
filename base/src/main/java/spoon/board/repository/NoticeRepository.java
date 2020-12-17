package spoon.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.board.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>, QueryDslPredicateExecutor<Notice> {

}
