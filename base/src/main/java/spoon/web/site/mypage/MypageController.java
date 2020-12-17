package spoon.web.site.mypage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.common.utils.WebUtils;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class MypageController {

    private MemberService memberService;

    @RequestMapping(value = "mypage", method = RequestMethod.GET)
    public String mypage(ModelMap map) {
        Member member = memberService.getMember(WebUtils.userid());
        map.addAttribute("member", member);
        map.addAttribute("recommList", memberService.getRecommList(member.getUserid()));

        return "site/mypage/mypage";
    }

}
