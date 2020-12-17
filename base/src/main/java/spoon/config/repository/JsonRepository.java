package spoon.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import spoon.config.entity.JsonConfig;

public interface JsonRepository extends JpaRepository<JsonConfig, String>, QueryDslPredicateExecutor<JsonConfig> {

}
