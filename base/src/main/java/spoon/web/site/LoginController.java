package spoon.web.site;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.config.domain.ConfigDto;
import spoon.ip.service.IpAddrService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@AllArgsConstructor
@Controller
public class LoginController {

    private IpAddrService ipAddrService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String login(ModelMap map, @CookieValue(value = "role", required = false) String role,
                        @RequestParam(value = "error", required = false) String error,
                        HttpServletResponse response, SitePreference sitePreference, Device device) {

        // IP Address 체크
        if (Config.getSiteConfig().isIpUser() && ipAddrService.checkIp("user")) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

        if (!device.isNormal() && !sitePreference.isMobile()) {
            return "redirect:/?site_preference=mobile" + (StringUtils.notEmpty(error) ? "&error=" + error : "");
        }

        if (device.isNormal() && !sitePreference.isNormal()) {
            return "redirect:/?site_preference=normal" + (StringUtils.notEmpty(error) ? "&error=" + error : "");
        }

        if (StringUtils.notEmpty(error)) {
            map.addAttribute("message", WebUtils.logoutMessage(error));
        }

        if (StringUtils.empty(role)) {
            map.addAttribute("config", JsonUtils.toString(new ConfigDto.Join()));
            return "site/login";
        }

        Cookie cookie = new Cookie("role", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        if ("admin".equals(role)) {
            return "redirect:" + Config.getPathAdmin();
        } else if ("seller".equals(role)) {
            return "redirect:" + Config.getPathSeller();
        }

        map.addAttribute("config", JsonUtils.toString(new ConfigDto.Join()));

        return "site/login";
    }

    @RequestMapping("/block")
    public String block(ModelMap map) {
        map.addAttribute("message", Config.getSiteConfig().getBlockMessage());
        return "site/block";
    }

}
