package spoon.support.security;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spoon.config.domain.Config;
import spoon.ip.service.IpAddrService;
import spoon.member.domain.CurrentUser;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@Service
public class LoginUserService implements UserDetailsService {

    private MemberService memberService;

    private IpAddrService ipAddrService;

    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String role = request.getParameter("role");
        if (role == null) role = "";

        Member member = memberService.getMember(username);

        if (member == null) {
            throw new UsernameNotFoundException(username + " 정보를 찾을 수 없습니다.");
        }

        if (member.isSecession()) {
            throw new UsernameNotFoundException(username + " 정보를 찾을 수 없습니다.");
        }

        switch (member.getRole()) {
            case DUMMY:
            case USER:
            case AGENCY1:
            case AGENCY2:
            case AGENCY3:
            case AGENCY4:
//                if (Config.getSiteConfig().isIpUser() && ipAddrService.checkIp("user")) {
//                    throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
//                }
//                if (!"".equals(role) && !"seller".equals(role)) {
//                    throw new UsernameNotFoundException(username + " 정보를 찾을 수 없습니다.");
//                }
//                break;

                if (Config.getSiteConfig().isIpUser() && ipAddrService.checkIp("user")) {
                    throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
                }
                if (!"".equals(role) && !"seller".equals(role)) {
                    throw new UsernameNotFoundException(username + " 정보를 찾을 수 없습니다.");
                }
                break;
            default:
                if (Config.getSiteConfig().isIpAdmin() && !ipAddrService.checkIp("admin")) {
                    throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
                }
                if (!"admin".equals(role)) {
                    throw new UsernameNotFoundException(username + " 정보를 찾을 수 없습니다.");
                }
                break;
        }

        // 로그인 성공
        CurrentUser user = new CurrentUser(member);
        return new LoginUser(user);
    }
}
