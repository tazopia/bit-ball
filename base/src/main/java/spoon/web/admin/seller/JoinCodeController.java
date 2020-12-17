package spoon.web.admin.seller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.common.utils.JsonUtils;
import spoon.member.service.MemberService;
import spoon.seller.domain.JoinCodeDto;
import spoon.seller.service.JoinCodeService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.joinCodeController")
@RequestMapping("#{config.pathAdmin}")
public class JoinCodeController {

    private JoinCodeService joinCodeService;

    private MemberService memberService;

    /**
     * 가입코드 페이지
     */
    @RequestMapping(value = "seller/joinCode", method = RequestMethod.GET)
    public String list(ModelMap map, JoinCodeDto.Command command) {
        map.addAttribute("list", joinCodeService.getList(command));
        map.addAttribute("agencies", JsonUtils.toString(memberService.getAgencyList()));
        return "admin/seller/joinCode";
    }

    @ResponseBody
    @RequestMapping(value = "seller/joinCode/add", method = RequestMethod.POST)
    public AjaxResult add(JoinCodeDto.Add add) {
        return joinCodeService.add(add);
    }

    @ResponseBody
    @RequestMapping(value = "seller/joinCode/update", method = RequestMethod.POST)
    public AjaxResult update(JoinCodeDto.Update update) {
        return joinCodeService.update(update);
    }

    @ResponseBody
    @RequestMapping(value = "seller/joinCode/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id) {
        return joinCodeService.delete(id);
    }

}
