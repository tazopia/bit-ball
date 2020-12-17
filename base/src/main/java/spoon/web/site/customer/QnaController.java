package spoon.web.site.customer;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.bet.service.BetListService;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.customer.domain.QnaDto;
import spoon.customer.entity.Qna;
import spoon.customer.service.QnaService;
import spoon.support.web.AjaxResult;

import java.util.Arrays;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class QnaController {

    private QnaService qnaService;

    private BetListService betListService;

    @RequestMapping(value = "customer/qna", method = RequestMethod.GET)
    public String list(ModelMap map,
                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "regDate") Pageable pageable) {
        Page<Qna> page = qnaService.page(WebUtils.userid(), pageable);
        page.getContent().forEach(x -> {
            if (x.isBet()) {
                x.setBets(betListService.listByBoard(Arrays.stream(x.getBetId()).mapToObj(Long::toString).collect(Collectors.joining(",")), WebUtils.userid()));
            }
        });
        map.addAttribute("page", qnaService.page(WebUtils.userid(), pageable));

        return "site/customer/qna/list";
    }

    @RequestMapping(value = "customer/qna/add", method = RequestMethod.GET)
    public String add(ModelMap map, QnaDto.Add add) {
        if (StringUtils.notEmpty(add.getBetId())) {
            add.setBets(betListService.listByBoard(add.getBetId(), WebUtils.userid()));
        }
        map.addAttribute("add", add);
        return "site/customer/qna/add";
    }

    @RequestMapping(value = "customer/qna/add", method = RequestMethod.POST)
    public String add(QnaDto.Add add, RedirectAttributes ra) {
        boolean success = qnaService.add(add);
        if (!success) {
            ra.addFlashAttribute("message", "상담 등록에 실패하였습니다.\n\n잠시 후 다시 이용하여 주세요.");
        }
        return "redirect:" + Config.getPathSite() + "/customer/qna";
    }

    @ResponseBody
    @RequestMapping(value = "customer/qna/view", method = RequestMethod.POST)
    public AjaxResult view(Long id) {
        return qnaService.view(id);
    }

    @ResponseBody
    @RequestMapping(value = "customer/qna/delete", method = RequestMethod.POST)
    public AjaxResult hidden(Long id) {
        return qnaService.hidden(id);
    }

}
