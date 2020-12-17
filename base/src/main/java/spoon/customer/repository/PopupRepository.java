package spoon.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.customer.entity.Popup;

public interface PopupRepository extends JpaRepository<Popup, Long>, QueryDslPredicateExecutor<Popup> {

    @Query(value = "SELECT MAX(p.id) FROM Popup p")
    Long maxId();

}
