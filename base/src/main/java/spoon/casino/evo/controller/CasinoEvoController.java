package spoon.casino.evo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spoon.casino.evo.service.CasinoEvoService;

@RequiredArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class CasinoEvoController {

    private final CasinoEvoService casinoEvoService;

    // popup 으로 만들어야 한다.
    @GetMapping("casino/evo")
    public String evoLobby(Model model) {
        model.addAttribute("casinoUrl", casinoEvoService.getGameUrl());
        return "site/casino/evo";
    }

}
