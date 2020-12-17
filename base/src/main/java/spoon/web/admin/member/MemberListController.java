package spoon.web.admin.member;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.common.utils.StringUtils;
import spoon.member.domain.MemberDto;
import spoon.member.service.MemberListService;

@AllArgsConstructor
@Controller("admin.memberListController")
@RequestMapping("#{config.pathAdmin}")
public class MemberListController {

    private MemberListService memberListService;

    @RequestMapping(value = "member/list", method = RequestMethod.GET)
    public String list(ModelMap map, MemberDto.Command command,
                       @PageableDefault(size = 50) Pageable pageable) {
        map.addAttribute("page", memberListService.list(command, pageable));
        map.addAttribute("lnb", memberListLnb(command));
        map.addAttribute("command", command);

        return "admin/member/list";
    }

    private String memberListLnb(MemberDto.Command param) {
        if (StringUtils.notEmpty(param.getSearchValue())) {
            return "list";
        }
        switch (param.getMode()) {
            case "disabled":
                return "disabled";
            case "secession":
                return "secession";
            case "dummy":
                return "dummy";
            case "agency":
                return "agency";
            default:
                return "list";
        }
    }
}
