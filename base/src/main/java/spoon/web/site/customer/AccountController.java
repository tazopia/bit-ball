package spoon.web.site.customer;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spoon.customer.service.QnaService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@RestController
@RequestMapping("#{config.pathSite}")
public class AccountController {

    private QnaService qnaService;

    @RequestMapping(value = "customer/account", method = RequestMethod.POST)
    public AjaxResult account() {
        return qnaService.account();
    }

}
