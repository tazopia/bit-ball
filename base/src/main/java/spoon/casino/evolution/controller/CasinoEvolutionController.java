package spoon.casino.evolution.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.casino.evolution.domain.CasinoEvolutionResult;
import spoon.casino.evolution.service.CasinoEvolutionService;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.WebUtils;
import spoon.gameZone.ZoneConfig;
import spoon.member.domain.CurrentUser;

@RequiredArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class CasinoEvolutionController {

    private final CasinoEvolutionService casinoEvolutionService;

    @RequestMapping(value = "/casino/evolution", method = RequestMethod.GET)
    public String list(ModelMap map) {
        CurrentUser currentUser = WebUtils.user();
        String casinoId = currentUser.getCasinoEvolutionId();
        CasinoEvolutionResult.User user = casinoEvolutionService.getUser(casinoId);
        if (user == null) {
            casinoEvolutionService.createUser(currentUser);
            user = casinoEvolutionService.getUser(casinoId);
        }
        map.addAttribute("casinoId", casinoId);
        map.addAttribute("balance", user.getBalance());
        map.addAttribute("lobby", casinoEvolutionService.getLobby());
        map.addAttribute("token", casinoEvolutionService.getToken(casinoId));
        map.addAttribute("game", ZoneConfig.getCasinoEvolution().getGameUrl());

        return "site/casino/evolution";
    }

    @RequestMapping(value = "/casino/slot", method = RequestMethod.GET)
    public String slot(ModelMap map) {
        CurrentUser currentUser = WebUtils.user();
        String casinoId = currentUser.getCasinoEvolutionId();
        CasinoEvolutionResult.User user = casinoEvolutionService.getUser(casinoId);
        if (user == null) {
            casinoEvolutionService.createUser(currentUser);
            user = casinoEvolutionService.getUser(casinoId);
        }
        map.addAttribute("casinoId", casinoId);
        map.addAttribute("balance", user.getBalance());
        map.addAttribute("slot", JsonUtils.toString(casinoEvolutionService.getSlot()));
        map.addAttribute("token", casinoEvolutionService.getToken(casinoId));
        map.addAttribute("game", ZoneConfig.getCasinoEvolution().getGameUrl());

        return "site/casino/slot";
    }

}
