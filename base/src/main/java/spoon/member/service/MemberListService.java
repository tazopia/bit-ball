package spoon.member.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.member.domain.MemberDto;
import spoon.member.domain.User;
import spoon.member.entity.Member;

import java.util.List;

public interface MemberListService {

    Page<Member> list(MemberDto.Command command, Pageable pageable);

    Page<Member> sellerList(MemberDto.AgencyCommand command, Pageable pageable);

    List<User> getDummyList();

    Iterable<Member> getExcel();

}
