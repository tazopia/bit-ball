package spoon.web.site.board;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.board.domain.BoardDto;
import spoon.board.domain.CommentDto;
import spoon.board.service.BoardService;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class BoardController {

    private BoardService boardService;

    @RequestMapping(value = "board/{code:free|notice|event}", method = RequestMethod.GET)
    public String list(ModelMap map, @PathVariable("code") String code,
                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        if ("free".equals(code)) {
            map.addAttribute("list", boardService.topList());
        }
        map.addAttribute("page", boardService.page(code, pageable));
        map.addAttribute("code", code);
        map.addAttribute("title", boardService.boardTitle(code));

        return "site/board/list";
    }

    @RequestMapping(value = "board/{code:free|notice|event}/{id}", method = RequestMethod.GET)
    public String view(ModelMap map, @PathVariable("code") String code, @PathVariable("id") Long id, @RequestParam(defaultValue = "1") int page) {
        map.addAttribute("board", boardService.findView(id));
        map.addAttribute("code", code);
        map.addAttribute("page", page);
        map.addAttribute("title", boardService.boardTitle(code));
        map.addAttribute("add", new CommentDto.Add());

        return "site/board/view";
    }

    @RequestMapping(value = "board/{code:free}/add", method = RequestMethod.GET)
    public String add(ModelMap map, BoardDto.Add add, @PathVariable("code") String code) {
        if (StringUtils.notEmpty(add.getBetId())) {
            add.setBets(boardService.getBet(add.getBetId()));
        }
        map.addAttribute("board", add);
        map.addAttribute("code", code);
        map.addAttribute("title", boardService.boardTitle(code));

        return "site/board/add";
    }

    @RequestMapping(value = "board/{code:free}/add", method = RequestMethod.POST)
    public String add(BoardDto.Add add, @PathVariable("code") String code, RedirectAttributes ra) {
        add.setCode(code);
        boolean success = boardService.add(add);
        if (!success) {
            ra.addAttribute("message", "게시물을 등록하지 못하였습니다.");
        }

        return "redirect:" + Config.getPathSite() + "/board/" + code;
    }
}
