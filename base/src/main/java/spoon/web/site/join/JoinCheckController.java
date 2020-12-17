package spoon.web.site.join;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spoon.member.service.JoinCheckService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@RestController
@RequestMapping("#{config.pathJoin}")
public class JoinCheckController {

    private JoinCheckService joinCheckService;

    @RequestMapping(value = "/userid", method = RequestMethod.POST)
    public AjaxResult checkUserid(String userid) {
        return joinCheckService.notExistUserid(userid);
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.POST)
    public AjaxResult checkNickname(String nickname) {
        return joinCheckService.notExistNickname(nickname);
    }

    @RequestMapping(value = "/phone", method = RequestMethod.POST)
    public AjaxResult checkPhone(String phone) {
        return joinCheckService.notExistPhone(phone);
    }

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public AjaxResult checkAccount(String account) {
        return joinCheckService.notExistAccount(account);
    }

    @RequestMapping(value = "/recommender", method = RequestMethod.POST)
    public AjaxResult checkRecommender(String recommender) {
        return joinCheckService.isExistRecommender(recommender);
    }
}
