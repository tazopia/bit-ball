package spoon.gate.controller;

import lombok.AllArgsConstructor;
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
import spoon.gate.service.GateService;

@AllArgsConstructor
@Controller("admin.gateAdminController")
@RequestMapping("#{config.pathAdmin}")
public class GateAdminController {

    private GateService gateService;

    @RequestMapping(value = "/gate/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getGate());

        return "admin/gate/config";
    }

    @RequestMapping(value = "/gate/config", method = RequestMethod.POST)
    public String config(GateConfig gateConfig, RedirectAttributes ra) {
        boolean success = gateService.updateGateConfig(gateConfig);

        if (success) {
            ra.addFlashAttribute("message", "게이트 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "게이트 설정 변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/gate/config";
    }

    @RequestMapping(value = "/gate/exchange", method = RequestMethod.GET)
    public String gateExchange(ModelMap map, GateDto.Command command,
                               @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "id") Pageable pageable) {
        map.addAttribute("command", command);
        map.addAttribute("page", gateService.page(command, pageable));

        return "admin/gate/exchange";
    }
}
