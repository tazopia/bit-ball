package spoon.banking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.service.MemberService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.PointCode;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class ExchangeServiceImpl implements ExchangeService {

    private MemberService memberService;

    private PaymentService paymentService;

    @Transactional
    @Override
    public AjaxResult exchange() {
        User user = memberService.getUser(WebUtils.userid());
        if (user == null || user.getRole() == Role.DUMMY) {
            return new AjaxResult(false, "권한이 부족합니다.");
        }

        long amount = user.getPoint();
        AjaxResult result = new AjaxResult();

        if (amount == 0) {
            result.setMessage("전환할 포인트가 없습니다.");
            return result;
        }

        int exchange = Config.getSiteConfig().getPoint().getExchange();
        if (amount < exchange) {
            result.setMessage("포인트 전환은 최소 " + String.format("%,d", exchange) + " 이상입니다.");
            return result;
        }

        try {
            paymentService.addMoney(MoneyCode.EXCHANGE, 0L, user.getUserid(), amount, "포인트 전환");
            paymentService.addPoint(PointCode.EXCHANGE, 0L, user.getUserid(), -amount, "포인트 전환");
            result.setSuccess(true);
            result.setValue(String.valueOf(amount));
        } catch (RuntimeException e) {
            result.setMessage("포인트 전환에 실패하였습니다.");
            log.error("포인트 전환에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }
}
