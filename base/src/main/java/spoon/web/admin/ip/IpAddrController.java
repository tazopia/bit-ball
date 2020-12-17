package spoon.web.admin.ip;

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
import spoon.ip.domain.IpAddrDto;
import spoon.ip.service.IpAddrService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.ipAddrController")
@RequestMapping(value = "#{config.pathAdmin}")
public class IpAddrController {

    private IpAddrService ipAddrService;

    @RequestMapping(value = "customer/ip/{code}", method = RequestMethod.GET)
    public String list(ModelMap map, @PathVariable("code") String code, IpAddrDto.Command command,
                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        command.setCode(code);
        map.addAttribute("page", ipAddrService.page(command, pageable));
        map.addAttribute("command", command);

        return "admin/customer/ip";
    }

    @ResponseBody
    @RequestMapping(value = "customer/ip/{code}", method = RequestMethod.POST)
    public AjaxResult add(@PathVariable("code") String code, IpAddrDto.Add add) {
        add.setCode(code);
        return ipAddrService.add(add);
    }

    @ResponseBody
    @RequestMapping(value = "customer/ip/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id) {
        return ipAddrService.delete(id);
    }

}
