package spoon.bet.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.bet.domain.BetDto;
import spoon.bet.domain.BetUserRate;
import spoon.mapper.BetMapper;
import spoon.member.domain.Role;
import spoon.member.entity.QMember;
import spoon.member.repository.MemberRepository;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class BetUserRateServiceImpl implements BetUserRateService {

    private BetMapper betMapper;

    private MemberRepository memberRepository;

    @Override
    public List<BetUserRate> getBetUserRate(BetDto.BetRate command) {
        return betMapper.userRateList(command);
    }

    @Override
    public long getBetUserRateTotal(BetDto.BetRate command) {
        QMember q = QMember.member;
        return memberRepository.count(q.role.eq(Role.USER).and(q.enabled.isTrue()).and(q.secession.isFalse()));
    }
}
