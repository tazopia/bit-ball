package spoon.mapper;

import spoon.payment.entity.AddMoney;
import spoon.payment.entity.AddPoint;

public interface PaymentMapper {

    void addMoney(AddMoney addMoney);

    void addPoint(AddPoint addPoint);
}
