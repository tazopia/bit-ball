package spoon.support.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import spoon.common.utils.JsonUtils;
import spoon.support.web.AjaxResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        AjaxResult result = new AjaxResult(false, "아이디와 비밀번호를 다시 확인 하세요.");

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(JsonUtils.toString(result));
        response.getWriter().flush();
    }
}
