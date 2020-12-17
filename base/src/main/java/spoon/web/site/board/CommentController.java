package spoon.web.site.board;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.board.domain.CommentDto;
import spoon.board.service.CommentService;
import spoon.config.domain.Config;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class CommentController {

    private CommentService commentService;

    @RequestMapping(value = "comment/add", method = RequestMethod.POST)
    public String add(CommentDto.Add add, RedirectAttributes ra) {
        boolean success = commentService.add(add);
        if (!success) {
            ra.addAttribute("message", "댓글 등록에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathSite() + "/board/" + add.getCode() + "/" + add.getBoardId() + "?page=" + add.getPage();
    }

    @ResponseBody
    @RequestMapping(value = "comment/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id) {
        return commentService.delete(id);
    }
}
