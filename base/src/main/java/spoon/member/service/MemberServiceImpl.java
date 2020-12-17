package spoon.member.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.mapper.MemberMapper;
import spoon.member.domain.Agency;
import spoon.member.domain.MemberDto;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.entity.Member;
import spoon.member.entity.QMember;
import spoon.member.repository.MemberRepository;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private MemberRepository memberRepository;

    private MemberMapper memberMapper;

    private static QMember q = QMember.member;

    @Override
    public Member getMember(String userid) {
        return memberRepository.findByUserid(userid);
    }

    @Override
    public User getUser(String userid) {
        return memberMapper.getUser(userid);
    }

    @Override
    public User getRandomUser() {
        return memberMapper.getRandomUser();
    }

    @Override
    public User getUserAndMemo(String userid) {
        return memberMapper.getUserAndMemo(userid);
    }

    // TODO agency
    @Override
    public List<Agency> getAgencyList() {
        return memberMapper.getAgencyList();
    }

    @Override
    public List<String> getAgency1List(String agency2) {
        return memberMapper.getAgency1List(agency2);
    }

    @Override
    public List<String> getAgency4List() {
        return memberMapper.getAgency4List();
    }

    @Override
    public List<MemberDto.Exchange> getExchangeList(MemberDto.Seller command) {
        return memberMapper.getExchangeList(command);
    }

    @Override
    public User getRecomm(String userid) {
        return memberMapper.getRecomm(userid);
    }

    @Override
    public Iterable<Member> getRecommList(String userid) {
        return memberRepository.findAll(q.recommender.eq(userid).and(q.role.eq(Role.USER)).and(q.enabled.isTrue()).and(q.secession.isFalse()));
    }

    @Transactional
    @Override
    public void update(Member member) {
        memberRepository.saveAndFlush(member);
    }

    @Override
    public boolean isExist(String userid) {
        return memberRepository.count(q.userid.eq(userid)) > 0;
    }

}
