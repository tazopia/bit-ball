package spoon.casino.evolution.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.casino.evolution.domain.CasinoEvolutionConfig;
import spoon.casino.evolution.domain.CasinoEvolutionDto;
import spoon.casino.evolution.service.CasinoEvolutionService;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.sun.domain.SunConfig;
import spoon.sun.domain.SunDto;

@RequiredArgsConstructor
@Controller("admin.casinoEvolutionAdminController")
@RequestMapping("#{config.pathAdmin}")
public class CasinoEvolutionAdminController {

    private final CasinoEvolutionService casinoEvolutionService;

    @RequestMapping(value = "/casino/evolution/config", method = RequestMethod.GET)
    public String config(ModelMap map) {
        map.addAttribute("config", ZoneConfig.getCasinoEvolution());

        return "admin/casino/evolution/config";
    }

    @RequestMapping(value = "/casino/evolution/config", method = RequestMethod.POST)
    public String config(CasinoEvolutionConfig config, RedirectAttributes ra) {
        boolean success = casinoEvolutionService.updateConfig(config);
        if (success) {
            ra.addFlashAttribute("message", "카지노 설정을 변경하였습니다.");
        } else {
            ra.addFlashAttribute("message", "카지노 설정 변경에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/casino/evolution/config";
    }

    @RequestMapping(value = "/casino/evolution/exchange", method = RequestMethod.GET)
    public String gateExchange(ModelMap map, CasinoEvolutionDto.Exchange command,
                               @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "id") Pageable pageable) {
        map.addAttribute("command", command);
        map.addAttribute("page", casinoEvolutionService.page(command, pageable));

        return "admin/casino/evolution/exchange";
    }

    @RequestMapping(value = "/casino/evolution/bet", method = RequestMethod.GET)
    public String casinoBet(ModelMap map, CasinoEvolutionDto.Command command,
                            @PageableDefault(size = 30, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        map.addAttribute("command", command);
        map.addAttribute("page", casinoEvolutionService.betting(command, pageable));

        return "admin/casino/evolution/bet";
    }
}
