package spoon.web.admin.board;

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
@Controller("admin.commentController")
@RequestMapping("#{config.pathAdmin}")
public class CommentController {

    private CommentService commentService;

    @RequestMapping(value = "comment/add", method = RequestMethod.POST)
    public String add(CommentDto.AdminAdd add, RedirectAttributes ra) {
        boolean success = commentService.adminAdd(add);

        if (success) {
            ra.addFlashAttribute("message", "댓글을 등록하였습니다.");
        } else {
            ra.addFlashAttribute("message", "댓글 등록에 실패하였습니다.");
        }
        return "redirect:" + Config.getPathAdmin() + "/board/" + add.getCode() + "/" + add.getBoardId();
    }

    @ResponseBody
    @RequestMapping(value = "comment/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id, Long boardId) {
        return commentService.deleteAdmin(id, boardId);
    }

    @ResponseBody
    @RequestMapping(value = "comment/hidden", method = RequestMethod.POST)
    public AjaxResult hidden(CommentDto.Hidden hidden) {
        return commentService.hidden(hidden);
    }

}
