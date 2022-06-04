package spoon.casino.evolution.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spoon.casino.evolution.domain.CasinoEvolutionDto;
import spoon.casino.evolution.domain.CasinoEvolutionResult;
import spoon.casino.evolution.service.CasinoEvolutionService;
import spoon.common.utils.WebUtils;
import spoon.support.web.AjaxResult;

import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("#{config.pathSite}")
public class CasinoEvolutionRestController {

    private final CasinoEvolutionService casinoEvolutionService;

    @PostMapping(value = "/casino/evolution/user")
    public CasinoEvolutionResult.User user() {
        String userid = Objects.requireNonNull(WebUtils.user()).getCasinoEvolutionId();
        return casinoEvolutionService.getUser(userid);
    }

    @PostMapping(value = "/casino/evolution/token")
    public CasinoEvolutionResult.Token token() {
        String userid = Objects.requireNonNull(WebUtils.user()).getCasinoEvolutionId();
        return casinoEvolutionService.getToken(userid);
    }

    @PostMapping(value = "/casino/evolution/send")
    public AjaxResult sendMoney(@RequestBody CasinoEvolutionDto.SendMoney send) {
        return casinoEvolutionService.sendMoney(send);
    }

    @PostMapping(value = "/casino/evolution/receive")
    public AjaxResult receiveMoney(@RequestBody CasinoEvolutionDto.ReceiveMoney receive) {
        return casinoEvolutionService.receiveMoney(receive);
    }

    @PostMapping(value = "/casino/evolution/exchange")
    public CasinoEvolutionResult.Exchange exchangeMoney(@RequestBody CasinoEvolutionDto.ReceiveMoney receive) {
        return casinoEvolutionService.exchangeMoney(receive);
    }

    @PostMapping(value = "/casino/evolution/game")
    public AjaxResult gameUrl(@RequestBody CasinoEvolutionDto.Game game) {
        return casinoEvolutionService.getGameUrl(game);
    }

    @PostMapping(value = "/casino/evolution/slot")
    public AjaxResult slotUrl(@RequestBody CasinoEvolutionDto.Game game) {
        return casinoEvolutionService.getGameUrl(game);
    }

}
