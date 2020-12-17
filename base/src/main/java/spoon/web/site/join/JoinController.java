package spoon.web.site.join;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;
import spoon.config.domain.ConfigDto;
import spoon.member.domain.MemberDto;
import spoon.member.service.MemberJoinService;
import spoon.support.web.AjaxResult;

import javax.servlet.http.HttpSession;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathJoin}")
public class JoinController {

    private MemberJoinService memberJoinService;

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public String code(ModelMap map) {
        map.addAttribute("config", JsonUtils.toString(new ConfigDto.Join()));
        return "site/join/code";
    }

    @ResponseBody
    @RequestMapping(value = "/code", method = RequestMethod.POST)
    public AjaxResult code(String joinCode, HttpSession session) {
        if (StringUtils.empty(joinCode)) {
            return new AjaxResult(false, "가입코드를 입력하세요.");
        }
        AjaxResult result = memberJoinService.findJoinCode(joinCode);
        if (result.isSuccess()) {
            session.setAttribute("joinCode", joinCode);
        }
        return result;
    }

    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public String join(ModelMap map, @SessionAttribute(name = "joinCode", required = false) String joinCode) {
        // 가입코드가 필수인데 가입코드가 없다면
        if (Config.getSiteConfig().getJoin().isJoinCodePage() && StringUtils.empty(joinCode)) {
            return "redirect:/";
        }
        MemberDto.Join member = new MemberDto.Join();
        member.setCode(joinCode == null ? "" : joinCode);
        map.addAttribute("member", member);
        map.addAttribute("banks", Config.getBanks());
        map.addAttribute("config", JsonUtils.toString(new ConfigDto.Join()));
        return "site/join/join";
    }

    @ResponseBody
    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public AjaxResult join(MemberDto.Join join) {
        return memberJoinService.join(join);
    }

}
