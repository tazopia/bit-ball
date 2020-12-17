package spoon.support.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LogoutHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Cookie[] cookies = request.getCookies();
        String error = request.getParameter("error");
        String url = "/";
        String role = "";

        for (Cookie cookie : cookies) {
            if ("role".equals(cookie.getName())) {
                role = cookie.getValue();
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                break;
            }
        }

        if ("admin".equals(role)) {
            url = Config.getPathAdmin();
        } else if ("seller".equals(role)) {
            url = Config.getPathSeller();
        }

        request.getSession().invalidate();

        url += (StringUtils.empty(error) ? "" : "?error=" + error);
        response.sendRedirect(url);
    }
}
