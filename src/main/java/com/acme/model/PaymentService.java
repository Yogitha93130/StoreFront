package com.acme.service;  // Fixed package - was incorrectly in model

import com.acme.model.Payment;
import com.acme.external.PaymentGateway;
import com.acme.external.PaymentResult;

public class PaymentService {
    private PaymentGateway paymentGateway;
    
    public PaymentService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
    
    public Payment processPayment(Payment payment) {
        PaymentResult result = paymentGateway.processPayment(
            payment.getAmount(), 
            payment.getPaymentMethod(), 
            payment.getCurrency()
        );
        
        if (result.isSuccess()) {
            payment.setStatus("COMPLETED");
            payment.setTransactionId(result.getTransactionId());
            payment.setSuccessful(true);
        } else {
            payment.setStatus("FAILED");
            payment.setErrorMessage(result.getMessage());
            payment.setSuccessful(false);
        }
        
        return payment;
    }
}
