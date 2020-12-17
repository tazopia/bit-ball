package spoon.web;

import lombok.AllArgsConstructor;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import spoon.config.domain.Config;
import spoon.config.domain.GameConfig;
import spoon.config.domain.SiteConfig;
import spoon.config.domain.SysConfig;

@AllArgsConstructor
@ControllerAdvice
public class ConfigAdvice {

    @ModelAttribute("sysConfig")
    public SysConfig sysConfig() {
        return Config.getSysConfig();
    }

    @ModelAttribute("gameConfig")
    public GameConfig gameConfig() {
        return Config.getGameConfig();
    }

    @ModelAttribute("siteConfig")
    public SiteConfig siteConfig() {
        return Config.getSiteConfig();
    }

    @ModelAttribute("mobile")
    public boolean isMobile(Device device) {
        return !device.isNormal();
    }

}
