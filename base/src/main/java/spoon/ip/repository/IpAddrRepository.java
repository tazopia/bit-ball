package spoon.ip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.ip.entity.IpAddr;

public interface IpAddrRepository extends JpaRepository<IpAddr, Long>, QueryDslPredicateExecutor<IpAddr> {

}
