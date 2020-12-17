package spoon.bet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.bet.entity.BetItem;

public interface BetItemRepository extends JpaRepository<BetItem, Long>, QueryDslPredicateExecutor<BetItem> {

}
