package spoon.web.admin.seller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.member.service.MemberService;
import spoon.sale.domain.SaleDto;
import spoon.sale.service.SaleService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Controller("admin.saleController")
@RequestMapping("#{config.pathAdmin}")
public class SaleController {

    private SaleService saleService;

    private MemberService memberService;

    /**
     * 총판 현재 정산금 페이지
     */
    @RequestMapping(value = "/seller/sale", method = RequestMethod.GET)
    public String sale(ModelMap map) {
        map.addAttribute("list", saleService.currentSale(new SaleDto.Command()));
        return "/admin/seller/sale/current";
    }

    /**
     * 총판 정산 작업하기
     */
    @ResponseBody
    @RequestMapping(value = "/seller/sale", method = RequestMethod.POST)
    public AjaxResult sale(String userid) {
        return saleService.balanceSale(userid);
    }

    /**
     * 총판 정산 리스트
     */
    @RequestMapping(value = "/seller/sale/list", method = RequestMethod.GET)
    public String list(ModelMap map, SaleDto.Command command, @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = {"regDate"}) Pageable pageable) {
        map.addAttribute("page", saleService.getPage(command, pageable));
        map.addAttribute("command", command);
        map.addAttribute("seller", memberService.getAgency4List());

        return "/admin/seller/sale/list";
    }

    /**
     * 총판 정산금 지급하기
     */
    @ResponseBody
    @RequestMapping(value = "/seller/sale/payment", method = RequestMethod.POST)
    public AjaxResult payment(SaleDto.Payment payment) {
        return saleService.payment(payment);
    }

}
