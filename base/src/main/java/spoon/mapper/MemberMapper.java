package spoon.mapper;

import spoon.member.domain.Agency;
import spoon.member.domain.MemberDto;
import spoon.member.domain.User;

import java.util.List;

public interface MemberMapper {

    List<User> getDummyList();

    List<Agency> getAgencyList();

    List<String> getAgency1List(String agency2);

    List<String> getAgency4List();

    List<MemberDto.Exchange> getExchangeList(MemberDto.Seller command);

    User getUser(String userid);

    User getUserAndMemo(String userid);

    User getRandomUser();

    User getRecomm(String userid);

}
