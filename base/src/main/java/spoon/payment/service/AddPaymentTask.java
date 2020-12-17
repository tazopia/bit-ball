package spoon.payment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class AddPaymentTask {

    private AddPaymentService addPaymentService;

    @Scheduled(fixedDelay = 500)
    public void addMoney() {
        addPaymentService.addMoney();
    }

    @Scheduled(fixedDelay = 500)
    public void addPoint() {
        addPaymentService.addPoint();
    }

}
