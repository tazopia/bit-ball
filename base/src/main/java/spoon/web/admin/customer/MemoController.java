package spoon.web.admin.customer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.customer.domain.MemoDto;
import spoon.customer.service.MemoService;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.memoController")
@RequestMapping(value = "#{config.pathAdmin}")
public class MemoController {

    private MemoService memoService;

    private MemberService memberService;

    /**
     * 쪽지 리스트
     */
    @RequestMapping(value = "customer/memo", method = RequestMethod.GET)
    public String list(ModelMap map, MemoDto.Command command,
                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        map.addAttribute("command", command);
        map.addAttribute("page", memoService.page(command, pageable));
        map.addAttribute("agencies", JsonUtils.toString(memberService.getAgencyList()));
        return "admin/customer/memo/list";
    }

    /**
     * 유저 한명한테 쪽지 보내기 폼
     */
    @RequestMapping(value = "customer/memo/add/{userid}", method = RequestMethod.GET)
    public String add(ModelMap map, @PathVariable("userid") String userid) {
        map.addAttribute("add", new MemoDto.One(userid));
        return "admin/customer/memo/popup";
    }

    /**
     * 유저 한명에게 쪽지 보내기
     */
    @ResponseBody
    @RequestMapping(value = "customer/memo/add", method = RequestMethod.POST)
    public AjaxResult add(MemoDto.One one) {
        return memoService.addOne(one);
    }

    /**
     * 다중 쪽지 폼
     */
    @RequestMapping(value = "customer/memo/memo", method = RequestMethod.GET)
    public String memo(ModelMap map) {
        map.addAttribute("add", new MemoDto.Add());
        map.addAttribute("agencies", JsonUtils.toString(memberService.getAgencyList()));
        return "admin/customer/memo/add";
    }

    /**
     * 다중 쪽지 보내기
     */
    @RequestMapping(value = "customer/memo/memo", method = RequestMethod.POST)
    public String memo(MemoDto.Add add, RedirectAttributes ra) {
        boolean success = memoService.add(add);
        if (success) {
            ra.addFlashAttribute("message", "쪽지 보내기를 완료 하였습니다.");
        } else {
            ra.addFlashAttribute("message", "쪽지 보내기에 실패 하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/customer/memo";
    }

    @ResponseBody
    @RequestMapping(value = "customer/memo/delete", method = RequestMethod.POST)
    public AjaxResult deleteAdmin(Long[] memoIds) {
        return memoService.deleteAdmin(memoIds);
    }


}
