package spoon.web.site;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.customer.service.PopupService;
import spoon.gameZone.power.service.PowerService;
import spoon.mapper.BoardMapper;
import spoon.mapper.GameMapper;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class MainController {

    private GameMapper gameMapper;

    private BoardMapper boardMapper;

    private PopupService popupService;

    private PowerService powerService;

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String main(ModelMap map, SitePreference preference) {
        if (!preference.isMobile()) {
            map.addAttribute("gameList", gameMapper.mainList());
            map.addAttribute("noticeList", boardMapper.mainNotice());
            map.addAttribute("eventList", boardMapper.mainEvent());
            map.addAttribute("powerball", powerService.getScore());
        }
        map.addAttribute("popup", popupService.getEnabled());

        return "/site/main";
    }

}
