package spoon.web.admin.member;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.member.domain.LoginDto;
import spoon.member.service.LoginHistoryService;

@AllArgsConstructor
@Controller("admin.loginListController")
@RequestMapping("#{config.pathAdmin}")
public class LoginListController {

    private LoginHistoryService loginHistoryService;

    @RequestMapping(value = "member/history", method = RequestMethod.GET)
    public String list(ModelMap map, LoginDto.Command command,
                       @PageableDefault(size = 50, sort = {"loginDate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        map.addAttribute("page", loginHistoryService.list(command, pageable));
        map.addAttribute("command", command);

        return "admin/member/history";
    }

}
