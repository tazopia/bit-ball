package spoon.web.admin.member;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.member.service.MemberChangeService;

@Slf4j
@AllArgsConstructor
@Controller("admin.memberChangeController")
@RequestMapping("#{config.pathAdmin}")
public class MemberChangeController {

    private MemberChangeService memberChangeService;

    /**
     * 기존 회원 마이그레이션
     */
    @RequestMapping(value = "system/change", method = RequestMethod.GET)
    public String memberChange() {
        return "admin/member/change";
    }

    /**
     * 기존 회원 마이그레이션 프로세스
     */
    @RequestMapping(value = "system/change", method = RequestMethod.POST)
    public String memberChange(ModelMap map, String memberText, RedirectAttributes ra) {
        ra.addFlashAttribute("list", memberChangeService.addMember(memberText));
        return "redirect:" + Config.getPathAdmin() + "/system/change/result";
    }

    /**
     * 기존 회원 마이그레이션 결과
     */
    @RequestMapping(value = "system/change/result", method = RequestMethod.GET)
    public String memberChage() {
        return "admin/member/result";
    }
}
