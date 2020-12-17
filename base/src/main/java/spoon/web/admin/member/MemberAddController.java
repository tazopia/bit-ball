package spoon.web.admin.member;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;
import spoon.member.domain.MemberDto;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberAddService;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.memberAddController")
@RequestMapping("#{config.pathAdmin}")
public class MemberAddController {

    private MemberService memberService;

    private MemberAddService memberAddService;

    @RequestMapping(value = "member/add", method = RequestMethod.GET)
    public String add(ModelMap map, @RequestParam(value = "userid", defaultValue = "") String userid) {
        MemberDto.Add add = new MemberDto.Add();

        if (StringUtils.notEmpty(userid)) {
            Member member = memberService.getMember(userid);
            switch (member.getRole()) {
                case AGENCY4:
                    add.setRole(Role.AGENCY3);
                    add.setAgency4(member.getUserid());
                    break;
                case AGENCY3:
                    add.setRole(Role.AGENCY2);
                    add.setAgency4(member.getAgency4());
                    add.setAgency3(member.getUserid());
                    break;
                case AGENCY2:
                    add.setRole(Role.AGENCY1);
                    add.setAgency4(member.getAgency4());
                    add.setAgency3(member.getAgency3());
                    add.setAgency2(member.getUserid());
                    break;
                case AGENCY1:
                    add.setRole(Role.USER);
                    add.setAgency4(member.getAgency4());
                    add.setAgency3(member.getAgency3());
                    add.setAgency2(member.getAgency2());
                    add.setAgency1(member.getUserid());
                    break;
            }
        }

        map.addAttribute("member", add);
        map.addAttribute("agencies", JsonUtils.toString(memberService.getAgencyList()));
        map.addAttribute("banks", Config.getBanks());
        map.addAttribute("pathJoin", Config.getPathJoin());
        return "admin/member/popup/add";
    }

    @ResponseBody
    @RequestMapping(value = "member/add", method = RequestMethod.POST)
    public AjaxResult add(MemberDto.Add add) {
        return memberAddService.adminAdd(add);
    }


    @RequestMapping(value = "member/dummy", method = RequestMethod.GET)
    public String dummy(ModelMap map) {
        map.addAttribute("dummy", new MemberDto.Dummy());
        return "admin/member/popup/dummy";
    }

    @ResponseBody
    @RequestMapping(value = "member/dummy", method = RequestMethod.POST)
    public AjaxResult dummy(MemberDto.Dummy dummy) {
        return memberAddService.adminAddDummy(dummy);
    }

}
