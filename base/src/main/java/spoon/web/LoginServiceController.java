package spoon.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.member.domain.Role;
import spoon.member.service.LoginHistoryService;
import spoon.payment.service.EventPaymentService;

import javax.servlet.http.Cookie;

@Slf4j
@AllArgsConstructor
@Controller
public class LoginServiceController {

    private LoginHistoryService loginHistoryService;

    private EventPaymentService pointPaymentService;

    @RequestMapping("_login_service_")
    public String loginService(@CookieValue(value = "role", defaultValue = "") String role) {

        // 로그인 히스토리 기록하기
        loginHistoryService.addHistory();

        // 로그인 포인트 지급하기
        pointPaymentService.loginPoint();

        return "redirect:" + defaultTargetUrl(role);
    }

    private String defaultTargetUrl(String role) {
        switch (role) {
            case "admin":
                return Config.getPathAdmin() + "/member/list";
            case "seller":
                return Config.getPathSeller() + "/sale/daily";
            default:
                return Config.getPathSite() + "/main";
        }
    }
}
