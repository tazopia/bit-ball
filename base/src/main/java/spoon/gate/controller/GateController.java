package spoon.gate.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import spoon.config.domain.Config;
import spoon.gate.service.GateService;
import spoon.support.web.AjaxResult;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class GateController {

    private GateService gateService;

    /**
     * 게이트에서 보내주는 프레임 높이를 받기위한 페이지
     */
    @RequestMapping(value = "/gate/frame", method = RequestMethod.GET)
    public String frame(ModelMap map, @RequestParam(value = "height", defaultValue = "0") int height) {
        map.addAttribute("height", height);
        return "site/gate/frame";
    }

    /**
     * 라이브 게임
     */
    @RequestMapping(value = "/gate/live", method = RequestMethod.GET)
    public String live(ModelMap map) {
        map.addAttribute("message", gateService.login());
        map.addAttribute("gameUrl", gateService.getLiveUrl());

        return "site/gate/live";
    }

    /**
     * 섯다 게임
     */
    @RequestMapping(value = "/gate/suda", method = RequestMethod.GET)
    public String suda(ModelMap map) {
        map.addAttribute("message", gateService.login());
        map.addAttribute("gameUrl", gateService.getSudaUrl());

        return "site/gate/suda";
    }

    /**
     * 그래프 게임
     */
    @RequestMapping(value = "/gate/graph", method = RequestMethod.GET)
    public String graph(ModelMap map) {
        map.addAttribute("message", gateService.login());
        map.addAttribute("gameUrl", gateService.getGraphUrl());

        return "site/gate/graph";
    }

    /**
     * 머니 교환 페이지
     */
    @RequestMapping(value = "/gate/exchange", method = RequestMethod.GET)
    public String exchange(ModelMap map) {
        map.addAttribute("message", gateService.login());
        map.addAttribute("gameMoney", gateService.getGameMoney());

        return "site/gate/exchange";
    }

    /**
     * 머니 교환 페이지 프로세스
     */
    @ResponseBody
    @RequestMapping(value = "/gate/exchange", method = RequestMethod.POST)
    public AjaxResult exchange(long money, int code) {
        return gateService.exchange(money, code);
    }

    /**
     * 프레임 URL 리턴
     */
    @ModelAttribute("frameUrl")
    public String frameUrl(HttpServletRequest request) {
        return "http://" + request.getServerName().replaceAll("host.", "") + Config.getPathSite() + "/gate/frame";
    }
}
