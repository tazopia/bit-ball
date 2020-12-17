package spoon.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import spoon.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, QueryDslPredicateExecutor<Member> {

    @Query(value = "SELECT MAX(m.userid) FROM Member m WHERE m.role = 'DUMMY' AND m.userid LIKE 'dm[_]%'")
    String maxDummyId();

    Member findByUserid(String userid);

    @Query(value = "SELECT m.rateCode FROM Member m WHERE m.userid = :userid")
    String getRateCode(@Param("userid") String userid);
}
