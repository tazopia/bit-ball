package spoon.web.site.api;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spoon.common.utils.WebUtils;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.service.MemberService;

@AllArgsConstructor
@RestController
@RequestMapping("#{config.pathSite}")
public class InfoController {

    private MemberService memberService;

    @RequestMapping(value = "/api/info", method = RequestMethod.POST)
    public User apiUserInfo() {
        String userid = WebUtils.userid();
        User info = memberService.getUserAndMemo(userid);
        if (info.getRole() == Role.DUMMY) {
            info.setMoney(1000000);
        }

        return info;
    }
}
