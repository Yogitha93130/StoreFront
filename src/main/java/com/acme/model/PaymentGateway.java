package com.acme.external;

public interface PaymentGateway {
    PaymentResult processPayment(double amount, String paymentMethod, String currency);
}
