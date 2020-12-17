package spoon.inPlay.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.inPlay.bet.domain.InPlayBetDto;
import spoon.inPlay.bet.service.InPlayBetService;
import spoon.support.web.AjaxResult;

@RequiredArgsConstructor
@Controller(value = "admin.InPlayBetListController")
@RequestMapping("#{config.pathAdmin}")
public class InPlayBetListController {

    private final InPlayBetService inPlayBetService;

    @GetMapping("/inplay/bet")
    public String betList(Model model, InPlayBetDto.Command command,
                          @PageableDefault(size = 50, direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("page", inPlayBetService.page(pageable, command));


        return "admin/inplay/bet/list";
    }

    /**
     * 관리자 베팅 취소
     */
    @ResponseBody
    @RequestMapping(value = "/inplay/bet/update", method = RequestMethod.POST)
    public AjaxResult update() {
        return new AjaxResult(false);
    }


}
