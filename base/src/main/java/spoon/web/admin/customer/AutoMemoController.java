package spoon.web.admin.customer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.customer.domain.AutoMemoDto;
import spoon.customer.entity.AutoMemo;
import spoon.customer.service.AutoMemoService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.autoMemoController")
@RequestMapping(value = "#{config.pathAdmin}")
public class AutoMemoController {

    private AutoMemoService autoMemoService;

    /**
     * 자동응답 리스트
     */
    @RequestMapping(value = "customer/auto", method = RequestMethod.GET)
    public String list(ModelMap map, AutoMemoDto.Command command) {
        command.setCode("고객응대");
        map.addAttribute("list", autoMemoService.list(command));
        return "admin/customer/auto";
    }

    /**
     * 자동응답 등록
     */
    @ResponseBody
    @RequestMapping(value = "customer/auto/add", method = RequestMethod.POST)
    public AjaxResult add(AutoMemo add) {
        add.setCode("고객응대");
        add.setEnabled(true);
        return autoMemoService.add(add);
    }

    /**
     * 자동응답 수정
     */
    @ResponseBody
    @RequestMapping(value = "customer/auto/update", method = RequestMethod.POST)
    public AjaxResult update(AutoMemoDto.Update update) {
        return autoMemoService.update(update);
    }

    /**
     * 자동응답 사용 사용하지 않기
     */
    @ResponseBody
    @RequestMapping(value = "customer/auto/enabled", method = RequestMethod.POST)
    public AjaxResult enabled(Long id) {
        return autoMemoService.enabled(id);
    }

    /**
     * 자동응답 삭제
     */
    @ResponseBody
    @RequestMapping(value = "customer/auto/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id) {
        return autoMemoService.delete(id);
    }

}
