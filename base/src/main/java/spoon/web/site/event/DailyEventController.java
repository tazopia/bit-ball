package spoon.web.site.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.common.utils.DateUtils;
import spoon.event.service.DailyEventService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class DailyEventController {

    private DailyEventService dailyEventService;

    @RequestMapping(value = "event/daily", method = RequestMethod.GET)
    public String daily(ModelMap map, String month) {
        if (month == null) {
            month = DateUtils.format(new Date(), "yyyy-MM");
        }
        map.addAttribute("month", month);
        map.addAttribute("calendar", dailyEventService.calendar(month));
        map.addAttribute("payment", dailyEventService.dailyPayment());
        map.addAttribute("week", dailyEventService.week(month));
        map.addAttribute("prev", dailyEventService.prev(month));
        map.addAttribute("next", dailyEventService.next(month));

        return "site/event/daily";
    }

    @ResponseBody
    @RequestMapping(value = "event/daily/payment", method = RequestMethod.POST)
    public AjaxResult payment(Long id) {
        return dailyEventService.enabled(id);
    }

    @ResponseBody
    @RequestMapping(value = "event/daily/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id) {
        return dailyEventService.deleted(id);
    }
}
