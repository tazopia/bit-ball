package spoon.web.admin.customer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.customer.domain.AutoMemoDto;
import spoon.customer.service.AutoMemoService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.joinMemoController")
@RequestMapping(value = "#{config.pathAdmin}")
public class JoinMemoController {

    private AutoMemoService autoMemoService;

    /**
     * 회원가입 자동쪽지
     */
    @RequestMapping(value = "customer/join", method = RequestMethod.GET)
    public String list(ModelMap map) {
        map.addAttribute("memo", autoMemoService.getJoin());
        return "admin/customer/join";
    }

    /**
     * 회원가입 자동쪽지
     */
    @ResponseBody
    @RequestMapping(value = "customer/join", method = RequestMethod.POST)
    public AjaxResult update(AutoMemoDto.Join join) {
        return autoMemoService.updateJoin(join);
    }

}
