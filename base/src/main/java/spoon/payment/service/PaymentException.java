package spoon.payment.service;

public class PaymentException extends RuntimeException {

    PaymentException(String message) {
        super(message);
    }
}
