package spoon.member.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.customer.service.MemoService;
import spoon.member.domain.MemberDto;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.monitor.service.MonitorService;
import spoon.payment.service.EventPaymentService;
import spoon.seller.service.JoinCodeService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class MemberJoinServiceImpl implements MemberJoinService {

    private JoinCheckService joinCheckService;

    private MemberService memberService;

    private MemoService memoService;

    private EventPaymentService eventPaymentService;

    private JoinCodeService joinCodeService;

    private BCryptPasswordEncoder passwordEncoder;

    private MonitorService monitorService;

    @Override
    public AjaxResult findJoinCode(String joinCode) {
//        AjaxResult result = new AjaxResult();
//
//        if (joinCodeService.existJoinCode(joinCode)) {
//            result.setSuccess(true);
//            result.setUrl(Config.getPathJoin() + "/join");
//        } else {
//            result.setMessage("가입코드가 일치하지 않습니다.");
//        }
//        return result;


        AjaxResult result = new AjaxResult();

        Member member = memberService.getMember(joinCode);
        if (member != null && !member.isSecession()) {
            result.setSuccess(true);
            result.setUrl(Config.getPathJoin() + "/join");
        } else {
            result.setMessage("가입코드가 일치하지 않습니다.");
        }
        return result;

    }

    @Transactional
    @Override
    public AjaxResult join(MemberDto.Join join) {
        AjaxResult result = new AjaxResult();

        // 동일 아이피로 가입 불가
        if (!joinCheckService.enabledCheckIp(WebUtils.ip())) {
            result.setMessage("동일 아이피에서 연속해서 회원가입을 하실 수 없습니다.");
            return result;
        }

        if (Config.getSiteConfig().getJoin().isJoinCodePage() && StringUtils.empty(join.getCode())) {
            result.setMessage("가입코드가 유효하지 않습니다.");
            return result;
        }

        if (StringUtils.empty(join.getUserid()) || join.getUserid().length() < 4) {
            result.setMessage("아이디는 최소 4글자 이상입니다.");
            return result;
        }

        if (!isUserid(join.getUserid())) {
            result.setMessage("아이디가 유효하지 않습니다.");
            return result;
        }

        if (!joinCheckService.notExistUserid(join.getUserid()).isSuccess()) {
            result.setMessage("아이디가 중복 되었습니다.");
            return result;
        }

        if (StringUtils.empty(join.getNickname()) || join.getNickname().length() < 2) {
            result.setMessage("닉네임은 최소 2글자 이상입니다.");
            return result;
        }

        if (!isNickname(join.getNickname())) {
            result.setMessage("닉네임이 유효하지 않습니다.");
            return result;
        }

        if (!joinCheckService.notExistNickname(join.getNickname()).isSuccess()) {
            result.setMessage("닉네임이 중복 되었습니다.");
            return result;
        }

        if (Config.getSiteConfig().getJoin().isDuplicatePhone() && !joinCheckService.notExistPhone(join.getPhone()).isSuccess()) {
            result.setMessage("휴대폰 번호가 유효하지 않습니다.");
            return result;
        }

        if (Config.getSiteConfig().getJoin().isDuplicateAccount() && !joinCheckService.notExistAccount(join.getAccount()).isSuccess()) {
            result.setMessage("계좌번호가 유효하지 않습니다.");
            return result;
        }

        if (Config.getSiteConfig().getJoin().isRequiredRecommend() && !joinCheckService.isExistRecommender(join.getRecommender()).isSuccess()) {
            result.setMessage("추천인이 유효하지 않습니다.");
            return result;
        }

        boolean success = addMember(join);

        if (!success) {
            result.setMessage("회원가입에 실패하였습니다. 잠시후 다시 이용하세요.");
            return result;
        }

        result.setMessage(Config.getSiteConfig().getJoin().getJoinAutoMessage());
        result.setSuccess(true);
        result.setUrl("/");

        return result;
    }

    private boolean addMember(MemberDto.Join join) {
        try {
            Member member = new Member();
            member.setUserid(join.getUserid());
            member.setNickname(join.getNickname());
            member.setPassword(passwordEncoder.encode(join.getPassword()));
            member.setPass(join.getPassword());
            member.setRole(Role.USER);
            member.setLevel(Config.getSiteConfig().getJoin().getJoinLevel());
            member.setPhone(join.getPhone().replaceAll("[^0-9]", ""));
            member.setPassKey(member.getPhone());
            member.setBank(join.getBank());
            member.setDepositor(join.getDepositor());
            member.setAccount(join.getAccount().replaceAll("[^0-9]", ""));
            member.setBankPassword(join.getBankPassword());
            member.setJoinDate(new Date());
            member.setJoinIp(WebUtils.ip());
            member.setLoginIp(WebUtils.ip());
            member.setJoinCode(join.getCode());
            member.setJoinDomain(WebUtils.domain());
            member.setEnabled(Config.getSiteConfig().getJoin().isJoinAutoAdmin());

            // 총판회원 가입인지 추천인 가입인지 확인한다.
            member.setRecommender(join.getRecommender());
            if (StringUtils.empty(member.getRecommender()) && StringUtils.notEmpty(join.getCode())) {
                Member joinCode = memberService.getMember(join.getCode());

                if (joinCode != null && joinCode.isEnabled() && !joinCode.isSecession()) {
                    member.setAgency1(joinCode.getAgency1());
                    member.setAgency2(joinCode.getAgency2());
                    member.setAgency3(joinCode.getAgency3());
                    member.setAgency4(joinCode.getAgency4());
                }
            }

            // 가입 포인트
            member.setPoint(Config.getSiteConfig().getJoin().getJoinPoint());
            if (member.getPoint() > 0) {
                eventPaymentService.joinPoint(member.getUser(), member.getPoint());
            }

            // 가입 축하 메시지
            if (Config.getJoinMemo().isEnabled()) {
                memoService.addJoin(member.getUser());
            }

            memberService.update(member);
        } catch (RuntimeException e) {
            log.error("회원등록 에러 : {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        monitorService.checkMember();
        return true;
    }

    private boolean isNickname(String nickname) {
        return nickname.matches("[0-9a-zA-Zㄱ-힣]{2,12}");
    }

    private boolean isUserid(String userid) {
        return userid.matches("[0-9a-zA-Z]{4,12}");
    }
}
