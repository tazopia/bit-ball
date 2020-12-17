package spoon.share.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import spoon.bet.entity.Bet;
import spoon.common.utils.StringUtils;
import spoon.game.domain.MenuCode;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.entity.Money;
import spoon.payment.service.PaymentService;
import spoon.sale.domain.SaleDto;
import spoon.share.repository.ShareMoneyRepository;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShareMoneyService {

    private final ShareMoneyRepository shareMoneyRepository;

    private final PaymentService paymentService;

    private final MemberService memberService;

    // 스포츠는 없다 롤링만이 있다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dividend(final Bet bet) {

        long amount = bet.getBetMoney();
        MenuCode code = bet.getMenuCode();

        long amount4 = 0;
        long amount3 = 0;
        long amount2 = 0;
        long amount1 = 0;
        long amount0 = 0;
        long amt = 0;

        double rate4 = 0D;
        double rate3 = 0D;
        double rate2 = 0D;
        double rate1 = 0D;
        double rate0 = 0D;

        double rate = 0D;

        // user
        Member user = memberService.getMember(bet.getUserid());
        rate0 = user.getRollingOdds(code, bet.getBetOdds());
        amount0 = (long) (amount * rate0 / 100D);
        rate = rate0;
        amt = amount0;
        if (amount0 > 0) {
            paymentService.addMoney(MoneyCode.ROLL_ZONE, bet.getId(), bet.getUserid(), amount0, getMemo(bet, rate0));
        }

        // agency1
        if (StringUtils.notEmpty(bet.getAgency1())) {
            Member agency1 = memberService.getMember(bet.getAgency1());
            if (agency1 != null && !agency1.isSecession()) {
                rate1 = agency1.getRollingOdds(code, bet.getBetOdds());
                amount1 = (long) (amount * rate1 / 100D);

                if (amount1 - amt > 0) {
                    double r = BigDecimal.valueOf(rate1).subtract(BigDecimal.valueOf(rate)).doubleValue();
                    paymentService.addMoney(MoneyCode.ROLL_ZONE, bet.getId(), bet.getAgency1(), amount1 - amt, getMemo(bet, r));

                    amt = amount1;
                    rate = rate1;
                }
            }
        }

        // agency2
        if (StringUtils.notEmpty(bet.getAgency2())) {
            Member agency2 = memberService.getMember(bet.getAgency2());
            if (agency2 != null && !agency2.isSecession()) {
                rate2 = agency2.getRollingOdds(code, bet.getBetOdds());
                amount2 = (long) (amount * rate2 / 100D);

                if (amount2 - amt > 0) {
                    double r = BigDecimal.valueOf(rate2).subtract(BigDecimal.valueOf(rate)).doubleValue();
                    paymentService.addMoney(MoneyCode.ROLL_ZONE, bet.getId(), bet.getAgency2(), amount2 - amt, getMemo(bet, r));

                    amt = amount2;
                    rate = rate2;
                }
            }
        }

        // agency3
        if (StringUtils.notEmpty(bet.getAgency3())) {
            Member agency3 = memberService.getMember(bet.getAgency3());
            if (agency3 != null && !agency3.isSecession()) {
                rate3 = agency3.getRollingOdds(code, bet.getBetOdds());
                amount3 = (long) (amount * rate3 / 100D);

                if (amount3 - amt > 0) {
                    double r = BigDecimal.valueOf(rate3).subtract(BigDecimal.valueOf(rate)).doubleValue();
                    paymentService.addMoney(MoneyCode.ROLL_ZONE, bet.getId(), bet.getAgency3(), amount3 - amt, getMemo(bet, r));

                    amt = amount3;
                    rate = rate3;
                }
            }
        }

        // agency4
        if (StringUtils.notEmpty(bet.getAgency4())) {
            Member agency4 = memberService.getMember(bet.getAgency4());
            if (agency4 != null && !agency4.isSecession()) {
                rate4 = agency4.getRollingOdds(code, bet.getBetOdds());
                amount4 = (long) (amount * rate4 / 100D);

                if (amount4 - amt > 0) {
                    double r = BigDecimal.valueOf(rate4).subtract(BigDecimal.valueOf(rate)).doubleValue();
                    paymentService.addMoney(MoneyCode.ROLL_ZONE, bet.getId(), bet.getAgency4(), amount4 - amt, getMemo(bet, r));
                }
            }
        }
    }

    private String getMemo(Bet bet, Double rate) {
        return bet.getUserid() + "님 " + bet.getMenuCode().getName() + " 베팅 " + bet.getBetMoney() + " - "
                + rate + " %";
    }

    public Page<Money> page(SaleDto.SaleCommand command, Pageable pageable) {
        return shareMoneyRepository.page(command, pageable);
    }
}
