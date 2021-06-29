package spoon.casino.evo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.casino.evo.domain.CasinoEvoCmd;
import spoon.casino.evo.domain.CasinoEvoConfig;
import spoon.casino.evo.service.CasinoEvoService;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.sun.domain.SunConfig;
import spoon.sun.domain.SunDto;

@RequiredArgsConstructor
@Controller("admin.casinoEvoAdminController")
@RequestMapping("#{config.pathAdmin}")
public class CasinoEvoAdminController {

    private final CasinoEvoService casinoEvoService;

    @RequestMapping(value = "/casino/evo/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getCasinoEvo());

        return "admin/casino/evo/config";
    }

    @RequestMapping(value = "/casino/evo/config", method = RequestMethod.POST)
    public String config(CasinoEvoConfig config, RedirectAttributes ra) {
        boolean success = casinoEvoService.updateConfig(config);
        if (success) {
            ra.addFlashAttribute("message", "선카지노 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "선카지노 설정 변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/casino/evo/config";
    }

    @RequestMapping(value = "/casino/evo/exchange", method = RequestMethod.GET)
    public String gateExchange(ModelMap map, CasinoEvoCmd.Command command,
                               @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "id") Pageable pageable) {
        map.addAttribute("command", command);
        map.addAttribute("page", casinoEvoService.page(command, pageable));

        return "admin/casino/evo/exchange";
    }
}
