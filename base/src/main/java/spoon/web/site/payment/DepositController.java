package spoon.web.site.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.banking.domain.BankingCode;
import spoon.banking.domain.DepositDto;
import spoon.banking.service.BankingDepositService;
import spoon.banking.service.BankingListService;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.config.domain.SiteConfig;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class DepositController {

    private MemberService memberService;

    private BankingListService bankingListService;

    private BankingDepositService bankDepositService;

    /**
     * 충전 신청 폼
     */
    @RequestMapping(value = "/payment/deposit", method = RequestMethod.GET)
    public String deposit(ModelMap map) {
        map.addAttribute("list", bankingListService.list(WebUtils.userid(), BankingCode.IN));
        map.addAttribute("deposit", new DepositDto.Add());
        map.addAttribute("zoneAmount", JsonUtils.toString(new DepositDto.Amount()));
        map.addAttribute("bonus", checkBonus());
        map.addAttribute("member", memberService.getMember(WebUtils.userid()));

        return "/site/payment/deposit";
    }

    /**
     * 충전 신청
     */
    @ResponseBody
    @RequestMapping(value = "/payment/deposit", method = RequestMethod.POST)
    public AjaxResult deposit(DepositDto.Add add) {
        return bankDepositService.deposit(add);
    }

    /**
     * 충전 신청 삭제
     */
    @ResponseBody
    @RequestMapping(value = "/payment/deposit/delete", method = RequestMethod.POST)
    public AjaxResult delete(long id) {
        return bankDepositService.delete(id);
    }

    /**
     * 충전 신청 취소
     */
    @ResponseBody
    @RequestMapping(value = "/payment/deposit/cancel", method = RequestMethod.POST)
    public AjaxResult cancel(long id) {
        return bankDepositService.cancel(id);
    }

    /**
     * 이벤트를 찾는다.
     */
    private String checkBonus() {
        int level = WebUtils.level();
        SiteConfig.Point point = Config.getSiteConfig().getPoint();
        StringBuilder sb = new StringBuilder();

        if (point.isEventPayment() && point.getEvent()[DateUtils.week()] && point.getEventRate()[DateUtils.week()] > 0) {
            if (point.isEventFirst()) {
                sb.append("[이벤트] 첫충전 보너스 ").append(point.getEventRate()[DateUtils.week()]).append("%가 진행중입니다.");
            } else {
                sb.append("[이벤트] 매충전 보너스 ").append(point.getEventRate()[DateUtils.week()]).append("%가 진행중입니다.");
            }
        } else {
            if (point.getFirstRate()[level] > 0) {
                sb.append(" 첫충전 보너스 ").append(point.getFirstRate()[level]).append("%");
            }
            if (point.getEveryRate()[level] > 0) {
                sb.append(sb.length() == 0 ? "" : " / ");
                sb.append(" 매충전 보너스 ").append(point.getEveryRate()[level]).append("%");
            }
        }
        return sb.toString();
    }
}
