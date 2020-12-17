package spoon.inPlay.web.site;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spoon.inPlay.bet.domain.InPlayBetDto;
import spoon.inPlay.bet.service.InPlayBetGameService;
import spoon.support.web.AjaxResult;

@RequiredArgsConstructor
@RestController
@RequestMapping("#{config.pathSite}")
public class InPlayBetController {

    private final InPlayBetGameService inPlayBetGameService;

    @PostMapping("inplay/betting")
    public AjaxResult betting(@RequestHeader(value = "AJAX") boolean ajax, @RequestBody InPlayBetDto.Bet bet) {

        if (!ajax) {
            return new AjaxResult(false, "페이지를 찾을 수 없습니다.");
        }

        return inPlayBetGameService.addGameBet(bet);
    }

    @PostMapping("/betting/inplay/delete")
    public AjaxResult delete(@RequestParam("id") long id) {
        return inPlayBetGameService.delete(id);
    }
}
