package spoon.web.seller.accounting;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.common.utils.WebUtils;
import spoon.member.domain.CurrentUser;
import spoon.member.domain.Role;
import spoon.seller.domain.Seller;
import spoon.seller.domain.SellerDto;
import spoon.seller.service.ShareService;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller("seller.shareController")
@RequestMapping("#{config.pathSeller}")
public class ShareController {

    private ShareService shareService;

    /**
     * 대리점 수수료율 관리
     */
    @RequestMapping(value = "accounting/share", method = RequestMethod.GET)
    public String shareRate(ModelMap map, SellerDto.AgencyCommand command) {
        CurrentUser user = WebUtils.user();
        command.setRole(user.getRole().name());
        command.setAgency(user.getUserid());

        List<Seller> list = shareService.agencySellerShare(command);

        map.addAttribute("hq", list.get(0));
        map.addAttribute("list", list);
        map.addAttribute("command", command);

        if (user.getRole() == Role.AGENCY4)
            return "/seller/accounting/share4";
        if (user.getRole() == Role.AGENCY3)
            return "/seller/accounting/share3";
        if (user.getRole() == Role.AGENCY2)
            return "/seller/accounting/share2";
        return "/seller/accounting/share1";
    }

    /**
     * 대리점 수수료율 수정
     */
    @ResponseBody
    @RequestMapping(value = "accounting/share", method = RequestMethod.POST)
    public AjaxResult shareRate(SellerDto.Update update) {
        return shareService.updatePartnerShare(update);
    }

}
