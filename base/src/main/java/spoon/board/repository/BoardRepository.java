package spoon.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>, QueryDslPredicateExecutor<Board> {

}
