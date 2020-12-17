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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.config.domain.EventLottoConfig;
import spoon.event.domain.LottoDto;
import spoon.event.service.LottoEventService;

@Slf4j
@AllArgsConstructor
@Controller("admin.lottoEventController")
@RequestMapping(value = "#{config.pathAdmin}")
public class LottoEventController {

    private LottoEventService lottoEventService;

    @RequestMapping(value = "event/lotto/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("eventConfig", Config.getEventLottoConfig());
        return "admin/event/lotto/config";
    }

    @RequestMapping(value = "event/lotto/config", method = RequestMethod.POST)
    public String config(EventLottoConfig eventLottoConfig, RedirectAttributes ra) {
        boolean success = lottoEventService.updateConfig(eventLottoConfig);
        if (success) {
            ra.addFlashAttribute("message", "로또 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "로또 설정 변경시 오류가 발생하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/event/lotto/config";
    }

    @RequestMapping(value = "event/lotto/payment", method = RequestMethod.GET)
    public String win(ModelMap map, @ModelAttribute LottoDto.Command command,
                      @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        map.addAttribute("page", lottoEventService.paymentPage(command, pageable));
        return "admin/event/lotto/payment";
    }

}
