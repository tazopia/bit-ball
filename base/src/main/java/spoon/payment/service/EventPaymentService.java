package spoon.payment.service;

import spoon.member.domain.User;

public interface EventPaymentService {

    void joinPoint(User user, Long point);

    void loginPoint();

    void boardPoint();

    void commentPoint();
}
