package spoon.web.admin.board;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.board.domain.NoticeDto;
import spoon.board.service.NoticeService;
import spoon.config.domain.Config;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller("admin.noticeController")
@RequestMapping("#{config.pathAdmin}")
public class NoticeController {

    private NoticeService noticeService;

    @RequestMapping(value = "/notice/list", method = RequestMethod.GET)
    public String list(ModelMap map) {
        map.addAttribute("list", noticeService.list());
        return "admin/notice/list";
    }

    @RequestMapping(value = "/notice/add", method = RequestMethod.GET)
    public String add(ModelMap map) {
        NoticeDto.Add add = new NoticeDto.Add();
        map.addAttribute("add", add);
        return "admin/notice/add";
    }

    @RequestMapping(value = "/notice/add", method = RequestMethod.POST)
    public String add(NoticeDto.Add add, RedirectAttributes ra) {
        noticeService.add(add);
        ra.addFlashAttribute("popup", "closing");
        return "redirect:" + Config.getPathAdmin() + "/notice/add";
    }

    @RequestMapping(value = "/notice/update/{id}", method = RequestMethod.GET)
    public String update(ModelMap map, @PathVariable("id") Long id) {
        map.addAttribute("id", id);
        map.addAttribute("update", new NoticeDto.Update(noticeService.get(id)));
        return "admin/notice/update";
    }

    @RequestMapping(value = "/notice/update/{id}", method = RequestMethod.POST)
    public String update(@PathVariable("id") Long id, NoticeDto.Update update, RedirectAttributes ra) {
        update.setId(id);
        noticeService.update(update);
        ra.addFlashAttribute("popup", "closing");
        return "redirect:" + Config.getPathAdmin() + "/notice/add";
    }

    @ResponseBody
    @RequestMapping(value = "/notice/delete", method = RequestMethod.POST)
    public AjaxResult delete(@RequestParam("id") Long id) {
        return noticeService.delete(id);
    }

}
