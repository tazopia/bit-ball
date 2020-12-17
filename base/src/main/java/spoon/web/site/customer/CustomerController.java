package spoon.web.site.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class CustomerController {

    @RequestMapping(value = "customer/guide/{menu}", method = RequestMethod.GET)
    public String guide(@PathVariable("menu") String menu) {
        return "site/customer/" + menu;
    }

}
