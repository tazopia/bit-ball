package spoon.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.payment.domain.MoneyDto;
import spoon.payment.domain.PaymentDto;
import spoon.payment.entity.Money;
import spoon.payment.entity.Point;

public interface PaymentListService {

    /**
     * 머니 내역을 페이지로 가져온다.
     */
    Page<Money> moneyPage(PaymentDto.Command command, Pageable pageable);

    Page<Money> sellerMoneyPage(MoneyDto.SellerCommand command, Pageable pageable);

    /**
     * 포인트 내역을 페이지로 가져온다.
     */
    Page<Point> pointPage(PaymentDto.Command command, Pageable pageable);

    Page<Point> pointPage(String userid, Pageable pageable);
}
