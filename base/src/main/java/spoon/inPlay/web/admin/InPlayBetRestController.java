package spoon.inPlay.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spoon.inPlay.bet.service.InPlayBetService;
import spoon.support.web.AjaxResult;

@RequiredArgsConstructor
@RestController(value = "admin.InPlayBetRestController")
@RequestMapping("#{config.pathAdmin}")
public class InPlayBetRestController {

    private final InPlayBetService inPlayBetService;

    @PostMapping("inplay/bet/hit")
    public AjaxResult goHit(@RequestParam("id") long id) {
        return inPlayBetService.goHit(id);
    }

    @PostMapping("inplay/bet/lose")
    public AjaxResult goLose(@RequestParam("id") long id) {
        return inPlayBetService.goLose(id);
    }

    @PostMapping("inplay/bet/exception")
    public AjaxResult goException(@RequestParam("id") long id) {
        return inPlayBetService.goException(id);
    }

    @PostMapping("inplay/bet/cancel")
    public AjaxResult goCancel(@RequestParam("id") long id) {
        return inPlayBetService.goCancel(id);
    }
}
