package spoon.web.site.customer;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.common.utils.WebUtils;
import spoon.customer.service.MemoService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class MemoController {

    private MemoService memoService;

    @RequestMapping(value = "customer/memo", method = RequestMethod.GET)
    public String list(ModelMap map,
                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        map.addAttribute("page", memoService.page(WebUtils.userid(), pageable));
        map.addAttribute("path", "memo");
        return "site/customer/memo";
    }

    @ResponseBody
    @RequestMapping(value = "customer/memo/view", method = RequestMethod.POST)
    public AjaxResult view(Long id) {
        return memoService.view(id);
    }

    @ResponseBody
    @RequestMapping(value = "customer/memo/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id) {
        return memoService.delete(id);
    }

    @ResponseBody
    @RequestMapping(value = "customer/memo/deleteAll", method = RequestMethod.POST)
    public AjaxResult delete(Long[] ids) {
        return memoService.delete(ids);
    }
}
