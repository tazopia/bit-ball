package spoon.web.admin.seller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.seller.domain.SellerDto;
import spoon.seller.service.ShareService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.shareController")
@RequestMapping("#{config.pathAdmin}")
public class ShareController {

    private ShareService shareService;

    /**
     * 총판 수수료율 관리
     */
    @RequestMapping(value = "/seller/share", method = RequestMethod.GET)
    public String shareRate(ModelMap map, SellerDto.Command command) {
        map.addAttribute("list", shareService.sellerShare(command));
        map.addAttribute("command", command);

        return "/admin/seller/share";
    }

    /**
     * 총판 수수료율 수정
     */
    @ResponseBody
    @RequestMapping(value = "/seller/share", method = RequestMethod.POST)
    public AjaxResult shareRate(SellerDto.Update update) {
        return shareService.updateShare(update);
    }

}
