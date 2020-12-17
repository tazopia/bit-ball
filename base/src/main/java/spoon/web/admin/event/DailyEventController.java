package spoon.web.admin.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.config.domain.EventConfig;
import spoon.event.domain.DailyDto;
import spoon.event.service.DailyEventService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.dailyEventController")
@RequestMapping(value = "#{config.pathAdmin}")
public class DailyEventController {

    private DailyEventService dailyEventService;

    @RequestMapping(value = "event/daily/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("eventConfig", Config.getEventConfig());
        return "admin/event/daily/config";
    }

    @RequestMapping(value = "event/daily/config", method = RequestMethod.POST)
    public String config(EventConfig eventConfig, RedirectAttributes ra) {
        boolean success = dailyEventService.updateConfig(eventConfig);
        if (success) {
            ra.addFlashAttribute("message", "출석체크 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "출석체크 설정 변경시 오류가 발생하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/event/daily/config";
    }

    @RequestMapping(value = "event/daily/win", method = RequestMethod.GET)
    public String win(ModelMap map, @ModelAttribute DailyDto.Command command,
                      @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        map.addAttribute("page", dailyEventService.paymentPage(command, pageable));
        return "admin/event/daily/win";
    }

    @ResponseBody
    @RequestMapping(value = "event/daily/payment", method = RequestMethod.POST)
    public AjaxResult payment(Long id) {
        return dailyEventService.payment(id);
    }

    @ResponseBody
    @RequestMapping(value = "event/daily/cancel", method = RequestMethod.POST)
    public AjaxResult cancel(Long id) {
        return dailyEventService.cancel(id);
    }

}
