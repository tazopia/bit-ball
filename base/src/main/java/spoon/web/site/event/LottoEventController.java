package spoon.web.site.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.event.service.LottoEventService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class LottoEventController {

    private LottoEventService lottoEventService;

    @ResponseBody
    @RequestMapping(value = "event/lotto", method = RequestMethod.POST)
    public AjaxResult payment() {
        return lottoEventService.lotto();
    }

}
