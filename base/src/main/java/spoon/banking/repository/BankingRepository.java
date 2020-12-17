package spoon.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.banking.entity.Banking;

public interface BankingRepository extends JpaRepository<Banking, Long>, QueryDslPredicateExecutor<Banking> {

    @Query(value = "SELECT ISNULL(SUM(amount), 0) FROM banking WHERE userid = :userid AND bankingCode = 'IN' AND closing = 1 AND cancel = 0 AND closingDate >= CONVERT(datetime, convert(varchar(10), GETDATE() ,120), 120)", nativeQuery = true)
    long getTodayDeposit(@Param("userid") String userid);

}
