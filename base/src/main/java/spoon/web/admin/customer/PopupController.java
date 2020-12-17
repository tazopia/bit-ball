package spoon.web.admin.customer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.customer.domain.PopupDto;
import spoon.customer.service.PopupService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.popupController")
@RequestMapping(value = "#{config.pathAdmin}")
public class PopupController {

    private PopupService popupService;

    /**
     * 팝업 관리 리스트
     */
    @RequestMapping(value = "/customer/popup", method = RequestMethod.GET)
    public String popup(ModelMap map) {
        map.addAttribute("list", popupService.getList());
        return "admin/customer/popup/list";
    }

    /**
     * 팝업 관리 등록 폼
     */
    @RequestMapping(value = "/customer/popup/add", method = RequestMethod.GET)
    public String addPopup(ModelMap map) {
        map.addAttribute("popup", new PopupDto.Add());
        return "admin/customer/popup/add";
    }

    /**
     * 팝업 관리 등록 프로세스
     */
    @RequestMapping(value = "/customer/popup/add", method = RequestMethod.POST)
    public String addPopup(PopupDto.Add add, MultipartFile image, RedirectAttributes ra) {
        popupService.add(add, image);
        ra.addFlashAttribute("closing", "closing");

        return "redirect:" + Config.getPathAdmin() + "/customer/popup/add";
    }


    /**
     * 팝업 관리 수정 프로세스
     */
    @RequestMapping(value = "/customer/popup/update/{id}", method = RequestMethod.GET)
    public String updatePopup(ModelMap map, @PathVariable("id") Long id) {
        map.addAttribute("popup", popupService.findOne(id));
        return "admin/customer/popup/update";
    }

    /**
     * 팝업 관리 수정 프로세스
     */
    @RequestMapping(value = "/customer/popup/update", method = RequestMethod.POST)
    public String updatePopup(PopupDto.Update update, MultipartFile image, RedirectAttributes ra) {
        popupService.update(update, image);
        ra.addFlashAttribute("closing", "closing");
        return "redirect:" + Config.getPathAdmin() + "/customer/popup/update/" + update.getId();
    }

    /**
     * 관리자 > 환경설정 > 종목설정 > 삭제 팝업 프로세스
     */
    @ResponseBody
    @RequestMapping(value = "/customer/popup/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id) {
        return popupService.delete(id);
    }

}
