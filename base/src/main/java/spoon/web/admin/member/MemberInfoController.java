package spoon.web.admin.member;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.bet.domain.BetUserInfo;
import spoon.bet.service.BetService;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.game.domain.MenuCode;
import spoon.member.domain.MemberDto;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.member.service.MemberUpdateService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller("admin.memberInfoController")
@RequestMapping("#{config.pathAdmin}")
public class MemberInfoController {

    private MemberService memberService;

    private MemberUpdateService memberUpdateService;

    private BetService betService;

    @RequestMapping(value = "/member/info/{userid}", method = RequestMethod.GET)
    public String memberInfo(ModelMap map, @PathVariable("userid") String userid) {
        Member member = memberService.getMember(userid);
        if (member == null) {
            map.addAttribute("message", "회원을 찾을 수 없습니다.");
            return "admin/popupError";
        } else if (WebUtils.role() != null && WebUtils.role().ordinal() < member.getRole().ordinal()) {
            map.addAttribute("message", "수정 권한이 없습니다.");
            return "admin/popupError";
        }
        map.addAttribute("member", member);
        map.addAttribute("agencies", JsonUtils.toString(memberService.getAgencyList()));
        map.addAttribute("banks", Config.getBanks());
        map.addAttribute("pathJoin", Config.getPathJoin());
        map.addAttribute("betInfo", new BetUserInfo(betService.userBetList(userid)));
        map.addAttribute("zoneMenu", MenuCode.getZoneList());

        if (member.getRole() == Role.USER) {
            map.addAttribute("recommList", memberService.getRecommList(userid));
        }

        return "admin/member/popup/info";
    }

    @ResponseBody
    @RequestMapping(value = "/member/update", method = RequestMethod.POST)
    public AjaxResult update(MemberDto.Update update) {
        return memberUpdateService.adminUpdate(update);
    }
}
