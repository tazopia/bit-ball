package spoon.support.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.member.domain.Role;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class BlockInterceptor extends HandlerInterceptorAdapter {

    /**
     * 사이트 서버점검시 관리자 외에 접근을 불허한다.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (Config.getSiteConfig().isBlock()) {
            Role role = WebUtils.role();
            if (role == null || role.getGroup() == Role.Group.AGENCY || role.getGroup() == Role.Group.USER && modelAndView != null) {
                request.getSession().invalidate();
                modelAndView.addObject("message", Config.getSiteConfig().getBlockMessage());
                modelAndView.setViewName("site/block");
            }
        }
        super.postHandle(request, response, handler, modelAndView);
    }
}
