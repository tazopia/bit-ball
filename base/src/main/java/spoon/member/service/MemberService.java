package spoon.member.service;

import spoon.member.domain.Agency;
import spoon.member.domain.MemberDto;
import spoon.member.domain.User;
import spoon.member.entity.Member;

import java.util.List;

public interface MemberService {

    /**
     * 회원 전체 정보를 가져온다.
     */
    Member getMember(String userid);

    /**
     * 회원의 추천인 정보를 가져온다.
     */
    User getRecomm(String userid);

    /**
     * 나를 추천한 회원 정보를 가져온다.
     */
    Iterable<Member> getRecommList(String userid);

    /**
     * 회원 정보를 가져온다.
     */
    User getUser(String userid);

    /**
     * 더미 유저 하나를 가져온다.
     */
    User getRandomUser();

    /**
     * 회원 정보와 쪽지 갯수를 가져온다.
     */
    User getUserAndMemo(String userid);

    /**
     * 총판 정보를 가져온다.
     */
    List<Agency> getAgencyList();

    /**
     * 대리점 정보만 가져온다.
     */
    List<String> getAgency1List(String agency2);

    /**
     * 총판 정보만 가져온다.
     */
    List<String> getAgency4List();

    /**
     * 포인트 전환 리스트
     */
    List<MemberDto.Exchange> getExchangeList(MemberDto.Seller command);

    /**
     * 회원 정보를 업데이트 한다.
     */
    void update(Member member);

    /**
     * 해당 아이디로 회원이 존재하는지 판단
     */
    boolean isExist(String userid);

}
