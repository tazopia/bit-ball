package spoon.web.admin.member;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.support.security.LoginUser;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Controller("admin.currentUserController")
@RequestMapping("#{config.pathAdmin}")
public class CurrentUserController {

    private SessionRegistry sessionRegistry;

    /**
     * 현재 접속자
     */
    @RequestMapping(value = "member/currentUser", method = RequestMethod.GET)
    public String currentUser(ModelMap map) {
        List<LoginUser> users = sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(LoginUser.class::cast)
                .collect(Collectors.toList());

        map.addAttribute("userList", users);
        return "admin/member/popup/currentUser";
    }

}
