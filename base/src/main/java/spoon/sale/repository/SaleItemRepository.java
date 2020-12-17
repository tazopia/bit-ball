package spoon.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.sale.entity.SaleItem;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long>, QueryDslPredicateExecutor<SaleItem> {

}
