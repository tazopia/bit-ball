package spoon.web.admin.bet;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import spoon.bet.service.BetResultService;
import spoon.support.web.AjaxResult;

@RequiredArgsConstructor
@RestController("admin.betResultController")
@RequestMapping("#{config.pathAdmin}")
public class BetResultController {

    private final BetResultService betResultService;

    /**
     * 관리자 적중처리
     */
    @RequestMapping(value = "/betting/win", method = RequestMethod.POST)
    public AjaxResult win(Long betId) {
        return betResultService.win(betId);
    }

    /**
     * 관리자 적특처리
     */
    @RequestMapping(value = "/betting/hit", method = RequestMethod.POST)
    public AjaxResult hit(Long betId) {
        return betResultService.hit(betId);
    }

    /**
     * 관리자 미적처리
     */
    @RequestMapping(value = "/betting/lose", method = RequestMethod.POST)
    public AjaxResult lose(Long betId) {
        return betResultService.lose(betId);
    }

    /**
     * 관리자 취소처리
     */
    @RequestMapping(value = "/betting/cancel", method = RequestMethod.POST)
    public AjaxResult cancel(Long betId) {
        return betResultService.cancel(betId);
    }
}
