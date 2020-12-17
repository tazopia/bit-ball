package spoon.web.admin.member;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spoon.member.service.MemberUpdateService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@RestController
@RequestMapping("#{config.pathAdmin}")
public class MemberUpdateController {

    private MemberUpdateService memberUpdateService;

    @RequestMapping(value = "/member/update/enabled", method = RequestMethod.POST)
    public AjaxResult enabled(String userid) {
        return memberUpdateService.enabled(userid);
    }

    @RequestMapping(value = "/member/update/black", method = RequestMethod.POST)
    public AjaxResult black(String userid) {
        return memberUpdateService.black(userid);
    }
}
