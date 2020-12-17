package spoon.sun.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spoon.common.utils.WebUtils;
import spoon.sun.domain.SunBalance;
import spoon.sun.domain.SunDto;
import spoon.sun.domain.SunGame;
import spoon.sun.service.SunService;
import spoon.support.web.AjaxResult;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("#{config.pathSite}")
public class SunRestController {

    private final SunService sunService;

    @PostMapping(value = "/sun/balance")
    public List<SunBalance> balance() {
        return sunService.getBalanceAll();
    }

    @PostMapping(value = "/sun/balance/{gameNo}")
    public SunBalance balance(@PathVariable int gameNo) {
        return sunService.getBalance(WebUtils.user().getSunid(), SunGame.numOf(gameNo));
    }

    @PostMapping(value = "/sun/send")
    public AjaxResult sendMoney(@RequestBody SunDto.SendMoney send) {
        return sunService.sendMoney(send);
    }

    @PostMapping(value = "/sun/receive")
    public AjaxResult receiveMoney(@RequestBody SunDto.ReceiveMoney receive) {
        return sunService.receiveMoney(receive);
    }

}
