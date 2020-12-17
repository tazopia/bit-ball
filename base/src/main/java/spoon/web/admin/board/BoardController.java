package spoon.web.admin.board;

import lombok.AllArgsConstructor;
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
import spoon.board.domain.BoardDto;
import spoon.board.domain.CommentDto;
import spoon.board.service.BoardAdminService;
import spoon.board.service.BoardService;
import spoon.config.domain.Config;
import spoon.member.service.MemberListService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller("admin.boardController")
@RequestMapping("#{config.pathAdmin}")
public class BoardController {

    private BoardService boardService;

    private BoardAdminService boardAdminService;

    private MemberListService memberListService;

    /**
     * 게시판 리스트
     */
    @RequestMapping(value = "/board/{code:free|notice|event}", method = RequestMethod.GET)
    public String list(ModelMap map, @PathVariable("code") String code, BoardDto.Command command,
                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        command.setCode(code);
        map.addAttribute("page", boardService.page(command, pageable));
        map.addAttribute("title", boardService.boardTitle(code));
        map.addAttribute("command", command);

        return "admin/board/list";
    }

    /**
     * 게시판 상세보기
     */
    @RequestMapping(value = "/board/{code:free|notice|event}/{id}", method = RequestMethod.GET)
    public String view(ModelMap map, @PathVariable("code") String code, @PathVariable("id") Long id, BoardDto.Command command) {
        command.setCode(code);
        command.setId(id);
        map.addAttribute("dummyList", memberListService.getDummyList());
        map.addAttribute("board", boardService.findView(command));
        map.addAttribute("title", boardService.boardTitle(code));
        map.addAttribute("command", command);
        map.addAttribute("add", new CommentDto.AdminAdd());

        return "admin/board/view";
    }

    /**
     * 게시판 수정 폼
     */
    @RequestMapping(value = "/board/{code:free|notice|event}/update/{id}", method = RequestMethod.GET)
    public String update(ModelMap map, @PathVariable("code") String code, @PathVariable("id") Long id) {
        map.addAttribute("dummyList", memberListService.getDummyList());
        map.addAttribute("board", new BoardDto.AdminUpdate(boardService.getOne(id)));
        map.addAttribute("title", boardService.boardTitle(code));
        return "admin/board/update";
    }

    /**
     * 게시판 수정하기
     */
    @RequestMapping(value = "/board/{code:free|notice|event}/update", method = RequestMethod.POST)
    public String add(@PathVariable("code") String code, BoardDto.AdminUpdate update, RedirectAttributes ra) {
        update.setCode(code);
        boolean success = boardAdminService.update(update);
        if (success) {
            ra.addFlashAttribute("message", "게시물을 수정하였습니다.");
        }
        ra.addFlashAttribute("message", "게시물 수정에 실패하였습니다.");

        return "redirect:" + Config.getPathAdmin() + "/board/" + code;
    }

    /**
     * 게시판 글쓰기 폼
     */
    @RequestMapping(value = "/board/{code:free|notice|event}/add", method = RequestMethod.GET)
    public String add(ModelMap map, @PathVariable("code") String code, BoardDto.AdminAdd add) {
        add.setCode(code);
        map.addAttribute("dummyList", memberListService.getDummyList());
        map.addAttribute("board", add);
        map.addAttribute("title", boardService.boardTitle(code));
        return "admin/board/add";
    }

    /**
     * 게시판 글쓰기
     */
    @RequestMapping(value = "/board/{code:free|notice|event}/add", method = RequestMethod.POST)
    public String add(@PathVariable("code") String code, BoardDto.AdminAdd add, RedirectAttributes ra) {
        add.setCode(code);
        boolean success = boardAdminService.add(add);
        if (success) {
            ra.addFlashAttribute("message", "게시물을 등록하였습니다.");
        } else {
            ra.addFlashAttribute("message", "게시물 등록에 실패하였습니다.");
        }

        return "redirect:" + Config.getPathAdmin() + "/board/" + code;
    }

    @ResponseBody
    @RequestMapping(value = "/board/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id) {
        return boardAdminService.delete(id);
    }

    @ResponseBody
    @RequestMapping(value = "/board/hidden", method = RequestMethod.POST)
    public AjaxResult hidden(BoardDto.Hidden hidden) {
        return boardAdminService.hidden(hidden);
    }

    @ResponseBody
    @RequestMapping(value = "/board/showTop", method = RequestMethod.POST)
    public AjaxResult showTop(BoardDto.Show show) {
        return boardAdminService.showTop(show);
    }

}
