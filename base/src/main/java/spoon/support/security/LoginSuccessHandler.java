package spoon.support.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import spoon.common.utils.JsonUtils;
import spoon.login.entity.Login;
import spoon.support.web.AjaxResult;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String role = request.getParameter("role");
        if (role == null) role = "";
        if (!"".equals(role)) {
            response.addCookie(new Cookie("role", role));
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        eventPublisher.publishEvent(Login.of(username, password));

        AjaxResult result = new AjaxResult(true);
        result.setUrl("/_login_service_");

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(JsonUtils.toString(result));
        response.getWriter().flush();
    }


}
