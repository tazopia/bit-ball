package spoon.support.interceptor;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@AllArgsConstructor
@Component
public class UserPostInterceptor extends HandlerInterceptorAdapter {

    private MemberService memberService;

    /**
     * 세션이 끊겼으면 logout 페이지로 팅겨 낸다.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!"true".equals(request.getHeader("AJAX"))) {
            if (WebUtils.userid() == null) {
                response.sendRedirect("/logout");
            }
        }
        return super.preHandle(request, response, handler);
    }

    /**
     * 회원 정보를 가져온다.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String userid = WebUtils.userid();

        // 더미, 회원, 총판
        if (userid != null && !"true".equals(request.getHeader("AJAX")) && request.getMethod().equals("GET")) {
            Role role = WebUtils.role();
            Assert.notNull(role, "권한은 null이 될 수 없습니다.");

            User user;
            switch (role) {
                case USER:
                case AGENCY1:
                case AGENCY2:
                case AGENCY3:
                case AGENCY4:
                    user = memberService.getUserAndMemo(userid);
                    // 짤린 회원이라면 짤라 낸다.
                    if (!user.isEnabled() || user.isSecession()) {
                        response.sendRedirect("/logout");
                    }
                    modelAndView.addObject("user", user);
                    break;
                default:
                    user = WebUtils.user(WebUtils.user());
                    if (role == Role.DUMMY) {
                        user.setMoney(10000000);
                    }
                    modelAndView.addObject("user", user);
                    break;
            }
            modelAndView.addObject("rolling", Config.getRolling().getJson());

            if (role == Role.USER && user.getMemo() > 0) {
                Object path = modelAndView.getModelMap().get("path");
                if (path == null || !"memo".equals(path.toString())) {
                    response.sendRedirect(Config.getPathSite() + "/customer/memo");
                }
            }
        }
        super.postHandle(request, response, handler, modelAndView);
    }
}
