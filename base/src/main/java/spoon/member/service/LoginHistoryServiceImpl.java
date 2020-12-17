package spoon.member.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.member.domain.CurrentUser;
import spoon.member.domain.LoginDto;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.entity.LoginHistory;
import spoon.member.entity.Member;
import spoon.member.entity.QLoginHistory;
import spoon.member.repository.LoginHistoryRepository;
import spoon.support.security.LoginUser;

@Slf4j
@AllArgsConstructor
@Service
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private MemberService memberService;

    private LoginHistoryRepository loginHistoryRepository;

    @Transactional
    @Override
    public void addHistory() {
        Member member = memberService.getMember(WebUtils.userid());
        if (member == null) return;

        try {
            User user = member.loginHistory();
            LoginHistory history = new LoginHistory(user);
            memberService.update(member);
            loginHistoryRepository.saveAndFlush(history);
            LoginUser loginUser = WebUtils.loginUser();
            if (loginUser != null) loginUser.setUser(new CurrentUser(member));
        } catch (RuntimeException e) {
            log.error("로그인 히스토리 기록에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Override
    public Page<LoginHistory> list(LoginDto.Command command, Pageable pageable) {
        QLoginHistory q = QLoginHistory.loginHistory;

        BooleanBuilder builder = new BooleanBuilder();

        // 권한별
        Role role = WebUtils.role();
        if (role == null) {
            builder.and(q.role.eq(Role.DUMMY));
        } else {
            switch (role) {
                case ADMIN:
                    builder.and(q.role.notIn(Role.GOD, Role.SUPER));
                    break;
                case SUPER:
                    builder.and(q.role.ne(Role.GOD));
                    break;
            }
        }

        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) {
                builder.and(q.userid.eq(command.getUsername()).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and(q.userid.like("%" + command.getUsername() + "%").or(q.nickname.eq("%" + command.getUsername() + "%")));
            }
        }

        if (StringUtils.notEmpty(command.getIp())) {
            builder.and(q.ip.like(command.getIp() + "%"));
        }

        if (StringUtils.notEmpty(command.getDevice())) {
            builder.and(q.device.eq(command.getDevice()));
        }

        if (StringUtils.notEmpty(command.getLoginDate())) {
            builder.and(q.loginDate.goe(DateUtils.start(command.getLoginDate()))).and(q.loginDate.lt(DateUtils.end(command.getLoginDate())));
        }

        return loginHistoryRepository.findAll(builder, pageable);
    }
}
