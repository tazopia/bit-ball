package spoon.web.admin.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.config.domain.SiteConfig;
import spoon.config.service.ConfigService;

@AllArgsConstructor
@Controller("admin.siteConfigController")
@RequestMapping("#{config.pathAdmin}")
public class SiteConfigController {

    private ConfigService configService;

    /**
     * 사이트설정 - 환경설정
     */
    @RequestMapping(value = "config/site", method = RequestMethod.GET)
    public String siteConfig() {
        return "admin/config/site";
    }

    /**
     * 사이트설정 - 환경설정 - 수정프로세스
     */
    @RequestMapping(value = "config/site", method = RequestMethod.POST)
    public String siteConfig(SiteConfig siteConfig, RedirectAttributes ra) {
        configService.updateSiteConfig(siteConfig);
        ra.addFlashAttribute("message", "사이트설정을 업데이트 하였습니다.");
        return "redirect:" + Config.getPathAdmin() + "/config/site";
    }
}
