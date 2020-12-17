package spoon.sun.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gate.domain.GateConfig;
import spoon.gate.domain.GateDto;
import spoon.sun.domain.SunConfig;
import spoon.sun.domain.SunDto;
import spoon.sun.service.SunService;

@RequiredArgsConstructor
@Controller("admin.sunAdminController")
@RequestMapping("#{config.pathAdmin}")
public class SunAdminController {

    private final SunService sunService;

    @RequestMapping(value = "/sun/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getSun());

        return "admin/sun/config";
    }

    @RequestMapping(value = "/sun/config", method = RequestMethod.POST)
    public String config(SunConfig sunConfig, RedirectAttributes ra) {
        boolean success = sunService.updateConfig(sunConfig);
        if (success) {
            ra.addFlashAttribute("message", "선카지노 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "선카지노 설정 변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/sun/config";
    }

    @RequestMapping(value = "/sun/exchange", method = RequestMethod.GET)
    public String gateExchange(ModelMap map, SunDto.Command command,
                               @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "id") Pageable pageable) {
        map.addAttribute("command", command);
        map.addAttribute("page", sunService.page(command, pageable));

        return "admin/sun/exchange";
    }
}
