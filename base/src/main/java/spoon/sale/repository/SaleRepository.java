package spoon.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.sale.entity.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long>, QueryDslPredicateExecutor<Sale> {

}
