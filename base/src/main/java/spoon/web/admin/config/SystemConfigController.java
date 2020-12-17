package spoon.web.admin.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.config.domain.SysConfig;
import spoon.config.service.ConfigService;

@AllArgsConstructor
@Controller("admin.systemConfigController")
@RequestMapping("#{config.pathAdmin}")
public class SystemConfigController {

    private ConfigService configService;

    /**
     * 환경설정 - 시스템 환경설정
     */
    @RequestMapping(value = "system/config", method = RequestMethod.GET)
    public String sysConfig() {
        return "admin/config/system";
    }

    /**
     * 환경설정 - 시스템 환경설정 - 수정프로세스
     */
    @RequestMapping(value = "system/config", method = RequestMethod.POST)
    public String sysConfig(SysConfig sysConfig, RedirectAttributes ra) {
        configService.updateSysConfig(sysConfig);
        ra.addFlashAttribute("message", "시스템 환경설정을 업데이트 하였습니다.");
        return "redirect:" + Config.getPathAdmin() + "/system/config";
    }

}
