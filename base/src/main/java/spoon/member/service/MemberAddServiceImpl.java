package spoon.member.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.NumberUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.member.domain.CurrentUser;
import spoon.member.domain.MemberDto;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.entity.QMember;
import spoon.member.repository.MemberRepository;
import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.PointCode;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class MemberAddServiceImpl implements MemberAddService {

    private MemberRepository memberRepository;

    private PaymentService paymentService;

    private BCryptPasswordEncoder passwordEncoder;

    private static QMember q = QMember.member;

    @Transactional
    @Override
    public AjaxResult adminAdd(MemberDto.Add add) {
        try {
            Member member = new Member();
            BeanUtils.copyProperties(add, member);

            member.setEnabled(true);
            member.setJoinDate(new Date());
            member.setJoinIp(WebUtils.ip());
            member.setPassword(passwordEncoder.encode(add.getPassword()));
            member.setPass(add.getPassword());
            member.setMoney(0L);
            member.setPoint(0L);

            String rateCode = memberRepository.getRateCode(member.getAgency4());

            // 총본사일 경우
            if (add.getRole() == Role.AGENCY4) {
                member.setAgency4(add.getUserid());
                member.setAgency3("");
                member.setAgency2("");
                member.setAgency1("");
                member.setRateCode("롤링분배");
                member.setLevel(14);
            }
            // 부본사일 경우
            if (add.getRole() == Role.AGENCY3) {
                member.setAgency4(add.getAgency4());
                member.setAgency3(add.getUserid());
                member.setAgency2("");
                member.setAgency1("");
                member.setRateCode(rateCode);
                member.setLevel(13);
            }
            // 총판일 경우
            if (add.getRole() == Role.AGENCY2) {
                member.setAgency4(add.getAgency4());
                member.setAgency3(add.getAgency3());
                member.setAgency2(add.getUserid());
                member.setAgency1("");
                member.setRateCode(rateCode);
                member.setLevel(12);
            }
            // 매장일 경우
            if (add.getRole() == Role.AGENCY1) {
                member.setAgency4(add.getAgency4());
                member.setAgency3(add.getAgency3());
                member.setAgency2(add.getAgency2());
                member.setAgency1(add.getUserid());
                member.setRateCode(rateCode);
                member.setLevel(11);
            }

            if (StringUtils.notEmpty(add.getPhone())) {
                member.setPhone(add.getPhone().replaceAll("[^0-9]", ""));
                member.setPassKey(member.getPhone());
            }
            if (StringUtils.notEmpty(add.getAccount())) {
                member.setAccount(add.getAccount().replaceAll("[^0-9]", ""));
            }
            memberRepository.saveAndFlush(member);

            // 관리자 머니 추가
            if (add.getMoney() > 0) {
                paymentService.addMoney(MoneyCode.ADD, 0L, add.getUserid(), add.getMoney(), "관리자 회원 생성 머니 지급");
            }

            // 관리자 포인트 추가
            if (add.getPoint() > 0) {
                paymentService.addPoint(PointCode.ADD, 0L, add.getUserid(), add.getPoint(), "관리자 회원 생성 포인트 지급");
            }
        } catch (RuntimeException e) {
            log.error("{} - 관리자 회원생성에 실패하였습니다.: {}", e.getMessage(), add.toString());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, add.getUserid() + "님 등록에 실패 하였습니다.");
        }

        return new AjaxResult(true, add.getUserid() + "님을 등록하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult sellerAdd(MemberDto.Agency add) {

        // role = 4 -> 3, role = 3 ->, role = 2 -> 1
        CurrentUser user = WebUtils.user();
        String rateCode = memberRepository.getRateCode(user.getAgency4());

        try {
            Member member = new Member();
            BeanUtils.copyProperties(add, member);
            member.setEnabled(true);
            member.setJoinDate(new Date());
            member.setJoinIp(WebUtils.ip());
            member.setPassword(passwordEncoder.encode(add.getPassword()));
            member.setPass(add.getPassword());
            member.setPass(add.getPassword());

            if (user.getRole() == Role.AGENCY4) {
                member.setRole(Role.AGENCY3);
                member.setAgency4(user.getUserid());
                member.setAgency3(add.getUserid());
                member.setAgency2("");
                member.setAgency1("");
                member.setRateCode(rateCode);
                member.setLevel(13);
            }

            if (user.getRole() == Role.AGENCY3) {
                member.setRole(Role.AGENCY2);
                member.setAgency4(user.getAgency4());
                member.setAgency3(user.getAgency3());
                member.setAgency2(add.getUserid());
                member.setAgency1("");
                member.setRateCode(rateCode);
                member.setLevel(12);
            }

            if (user.getRole() == Role.AGENCY2) {
                member.setRole(Role.AGENCY1);
                member.setAgency4(user.getAgency4());
                member.setAgency3(user.getAgency3());
                member.setAgency2(user.getAgency2());
                member.setAgency1(add.getUserid());
                member.setRateCode(rateCode);
                member.setLevel(11);
            }

            if (user.getRole() == Role.AGENCY1) {
                member.setRole(Role.USER);
                member.setAgency4(user.getAgency4());
                member.setAgency3(user.getAgency3());
                member.setAgency2(user.getAgency2());
                member.setAgency1(user.getAgency1());
                member.setLevel(Config.getSiteConfig().getJoin().getJoinLevel());
                member.setRateCode("");
            }

            if (StringUtils.notEmpty(add.getPhone())) {
                member.setPhone(add.getPhone().replaceAll("[^0-9]", ""));
                member.setPassKey(member.getPhone());
            }
            if (StringUtils.notEmpty(add.getAccount())) {
                member.setAccount(add.getAccount().replaceAll("[^0-9]", ""));
            }
            memberRepository.saveAndFlush(member);

        } catch (RuntimeException e) {
            log.error("{} - 대리점 회원생성에 실패하였습니다.: {}", e.getMessage(), add.toString());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, add.getUserid() + "님 등록에 실패 하였습니다.");
        }

        return new AjaxResult(true, add.getUserid() + "님을 등록하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult adminAddDummy(MemberDto.Dummy dummy) {
        String maxUserid = memberRepository.maxDummyId();
        if (maxUserid == null) maxUserid = "0";
        int maxId = Integer.parseInt(maxUserid.replaceAll("[^0-9]", ""));
        int update = 0;
        Set<String> dummyLine = new HashSet<>(Arrays.asList(dummy.getText().split(System.lineSeparator())));
        for (String nick : dummyLine) {
            if (StringUtils.empty(nick) || nick.length() < 2) continue;

            long count = memberRepository.count(q.nickname.eq(nick));
            if (count == 0) {
                update++;
                Member member = new Member();
                member.setUserid("dm_" + String.format("%04d", maxId + update));
                member.setNickname(nick);
                member.setRole(Role.DUMMY);
                member.setLevel(NumberUtils.random(dummy.getStart(), dummy.getEnd()));
                member.setJoinDate(new Date());
                member.setJoinIp(WebUtils.ip());
                member.setPassword(passwordEncoder.encode(""));
                memberRepository.saveAndFlush(member);
            }
        }
        return new AjaxResult(true, "전체 중복제외 " + dummyLine.size() + "명 중 " + update + "명의 게시판회원을 등록하였습니다.");
    }

}
