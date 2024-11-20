package com.lmw.exception;

public class PaymentFailureException extends RuntimeException {
    public PaymentFailureException(String message) {
        super(message);
    }
}
