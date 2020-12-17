package spoon.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.game.entity.Sports;

public interface SportsRepository extends JpaRepository<Sports, Long>, QueryDslPredicateExecutor<Sports> {

    @Query(value = "SELECT MAX(s.sort) FROM Sports s")
    Integer maxSort();

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Sports s SET s.sort = s.sort - 1 WHERE s.sort > :sort")
    void sortUpPrevAll(@Param("sort") int sort);
}
