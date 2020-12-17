package spoon.payment.service;

import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.PaymentDto;
import spoon.payment.domain.PointCode;
import spoon.support.web.AjaxResult;

public interface PaymentService {

    boolean notPaidMoney(MoneyCode moneyCode, long actionId, String userid);

    boolean notPaidPoint(PointCode pointCode, long actionId, String userid);

    AjaxResult addMoney(PaymentDto.Add add);

    AjaxResult addPoint(PaymentDto.Add add);

    AjaxResult exchange(PaymentDto.Add add, String bankPassword);

    long addMoney(MoneyCode moneyCode, Long actionId, String userid, long amount, String memo);

    long addPoint(PointCode pointCode, Long actionId, String userid, long amount, String memo);

    long rollbackMoney(MoneyCode moneyCode, Long actionId, String userid);

    long rollbackPoint(PointCode pointCode, Long actionId, String userid);

    long rollbackRecomm(PointCode pointCode, Long actionId, String userid);

}
