package spoon.web.site.bet;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.bet.domain.BetDto;
import spoon.bet.service.BetListService;
import spoon.bet.service.BetService;
import spoon.common.utils.WebUtils;
import spoon.inPlay.bet.service.InPlayBetService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class BetController {

    private BetListService betListService;

    private InPlayBetService inPlayBetService;

    private BetService betService;

    /**
     * 사용자 베팅 내역
     */
    @RequestMapping(value = "betting", method = RequestMethod.GET)
    public String list(ModelMap map, BetDto.UserCommand command,
                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "betDate") Pageable pageable) {
        String userid = WebUtils.userid();
        command.setUserid(userid);
        map.addAttribute("command", command);
        map.addAttribute("page", betListService.userPage(command, pageable));

        return "site/betting/list";
    }

    @RequestMapping(value = "betting/inplay", method = RequestMethod.GET)
    public String inplay(ModelMap map, BetDto.UserCommand command,
                         @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "betDate") Pageable pageable) {
        String userid = WebUtils.userid();
        command.setUserid(userid);
        map.addAttribute("page", inPlayBetService.userPage(command, pageable));
        return "site/betting/inplay";
    }

    /**
     * 사용자 베팅 취소
     */
    @ResponseBody
    @RequestMapping(value = "betting/cancel", method = RequestMethod.POST)
    public AjaxResult cancel(Long id) {
        return betService.cancelUser(id);
    }

    /**
     * 사용자 게임존 베팅 취소
     */
    @ResponseBody
    @RequestMapping(value = "betting/zone/cancel", method = RequestMethod.POST)
    public AjaxResult zoneCancel(@RequestBody BetDto.BetZoneCancel cancel) {
        return betService.cancelUser(cancel.getId());
    }

    /**
     * 사용자 베팅 삭제
     */
    @ResponseBody
    @RequestMapping(value = "betting/delete", method = RequestMethod.POST)
    public AjaxResult delete(BetDto.Delete command) {
        command.setUserid(WebUtils.userid());
        return betService.delete(command);
    }

    /**
     * 사용자 종료내역 전체삭제
     */
    @ResponseBody
    @RequestMapping(value = "betting/delete/closing", method = RequestMethod.POST)
    public AjaxResult delete() {
        return betService.deleteClosing(WebUtils.userid());
    }
}
