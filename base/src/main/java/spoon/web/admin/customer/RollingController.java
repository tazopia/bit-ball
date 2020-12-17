package spoon.web.admin.customer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.config.entity.JsonConfig;
import spoon.config.service.ConfigService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.rollingController")
@RequestMapping(value = "#{config.pathAdmin}")
public class RollingController {

    private ConfigService configService;

    /**
     * 상단 한줄 공지를 수정 페이지
     */
    @RequestMapping(value = "customer/rolling", method = RequestMethod.GET)
    public String rolling() {
        return "admin/customer/rolling";
    }

    /**
     * 상단 한줄 공지 수정
     */
    @ResponseBody
    @RequestMapping(value = "customer/rolling", method = RequestMethod.POST)
    public AjaxResult rolling(JsonConfig jsonConfig) {
        return configService.updateRolling(jsonConfig);
    }
}
