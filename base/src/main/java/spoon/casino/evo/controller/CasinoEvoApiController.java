package spoon.casino.evo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spoon.casino.evo.domain.CasinoEvoCmd;
import spoon.casino.evo.domain.CasinoEvoResult;
import spoon.casino.evo.service.CasinoEvoApiService;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CasinoEvoApiController {

    private final CasinoEvoApiService casinoEvoApiService;

    @GetMapping("api/balance")
    public CasinoEvoResult balance(CasinoEvoCmd.Balance balance, HttpServletRequest request) {
        return casinoEvoApiService.balance(balance);
    }

    @GetMapping("api/change")
    public CasinoEvoResult change(CasinoEvoCmd.Change change, HttpServletRequest request) {
        return casinoEvoApiService.change(change);
    }

    @GetMapping("api/cancel")
    public CasinoEvoResult cancel(CasinoEvoCmd.Cancel cancel, HttpServletRequest request) {
        return casinoEvoApiService.cancel(cancel);
    }

}
