package spoon.sun.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.sun.domain.SunGame;
import spoon.sun.service.SunService;

@RequiredArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class SunController {

    private final SunService sunService;

    @RequestMapping(value = "/sun", method = RequestMethod.GET)
    public String list(ModelMap map) {
        String gnum = sunService.getGnum();
        map.addAttribute("gnum", gnum);
        map.addAttribute("slot", sunService.getSlotUrl(gnum));
        map.addAttribute("live", sunService.getLiveUrl(gnum));
        map.addAttribute("game", SunGame.values());

        return "site/sun/list";
    }

}
