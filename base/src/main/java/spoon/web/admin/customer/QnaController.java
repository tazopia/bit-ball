package spoon.web.admin.customer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.customer.domain.AutoMemoDto;
import spoon.customer.domain.QnaDto;
import spoon.customer.service.QnaService;

@Slf4j
@AllArgsConstructor
@Controller("admin.qnaController")
@RequestMapping(value = "#{config.pathAdmin}")
public class QnaController {

    private QnaService qnaService;

    @RequestMapping(value = "customer/qna", method = RequestMethod.GET)
    public String list(ModelMap map, QnaDto.Command command,
                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        map.addAttribute("command", command);
        map.addAttribute("page", qnaService.page(command, pageable));
        return "admin/customer/qna/list";
    }

    @RequestMapping(value = "customer/qna/{id}", method = RequestMethod.GET)
    public String list(ModelMap map, @PathVariable("id") Long id) {
        map.addAttribute("qna", qnaService.getOne(id));
        map.addAttribute("auto", Config.getAutoMemoMap());
        return "admin/customer/qna/view";
    }

    @ResponseBody
    @RequestMapping(value = "customer/qna/auto", method = RequestMethod.POST)
    public AutoMemoDto.Reply reply(QnaDto.Auto auto) {
        return new AutoMemoDto.Reply(auto);
    }

    @RequestMapping(value = "customer/qna/reply", method = RequestMethod.POST)
    public String reply(QnaDto.Reply reply, RedirectAttributes ra) {
        boolean success = qnaService.reply(reply);
        if (success) {
            ra.addFlashAttribute("message", "답변 처리를 완료 하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/customer/qna";
    }

    @RequestMapping(value = "customer/qna/delete", method = RequestMethod.POST)
    public String reply(Long id, RedirectAttributes ra) {
        boolean success = qnaService.delete(id);
        if (success) {
            ra.addFlashAttribute("message", "고객센터 게시물을 삭제하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/customer/qna";
    }
}
