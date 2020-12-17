package spoon.web.admin;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.ip.service.IpAddrService;

@AllArgsConstructor
@Controller("admin.loginController")
@RequestMapping("#{config.pathAdmin}")
public class LoginController {

    private IpAddrService ipAddrService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String login(ModelMap map, @RequestParam(value = "error", required = false) String error, SitePreference sitePreference) {

        if (Config.getSiteConfig().isIpAdmin() && !ipAddrService.checkIp("admin")) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

        // 관리자는 모바일이 없다
        if (!sitePreference.isNormal()) {
            return "redirect:" + Config.getPathAdmin() + "?site_preference=normal" + (StringUtils.notEmpty(error) ? "&error=" + error : "");
        }

        if (StringUtils.notEmpty(error)) {
            map.addAttribute("message", WebUtils.logoutMessage(error));
        }

        return "admin/login";
    }
}
