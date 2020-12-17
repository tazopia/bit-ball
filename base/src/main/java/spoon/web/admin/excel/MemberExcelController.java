package spoon.web.admin.excel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import spoon.member.service.MemberListService;
import spoon.web.admin.excel.view.MemberExcelView;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathAdmin}")
public class MemberExcelController {

    private MemberExcelView memberExcelView;

    private MemberListService memberListService;

    @RequestMapping(value = "/excel/member", method = RequestMethod.GET)
    public ModelAndView memberExcel(ModelMap map) {
        map.addAttribute("members", memberListService.getExcel());
        return new ModelAndView(memberExcelView, map);
    }

}
