package com.acme.service;

import com.acme.model.Payment;
import com.acme.external.PaymentGateway;
import com.acme.external.PaymentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Stub implementation for testing
class SuccessfulPaymentGatewayStub implements PaymentGateway {
    @Override
    public PaymentResult processPayment(double amount, String paymentMethod, String currency) {
        return new PaymentResult(true, "txn_success_123", "Payment processed successfully");
    }
}

class FailedPaymentGatewayStub implements PaymentGateway {
    @Override
    public PaymentResult processPayment(double amount, String paymentMethod, String currency) {
        return new PaymentResult(false, null, "Insufficient funds");
    }
}

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        // No mocks needed - we're using stubs
    }

    @Test
    void processPayment_WithSuccessfulPaymentStub_ShouldReturnSuccessfulPayment() {
        // Arrange - Use successful payment stub
        paymentService = new PaymentService(new SuccessfulPaymentGatewayStub());
        Payment payment = new Payment(100.0, "credit_card", "USD");
        
        // Act
        Payment result = paymentService.processPayment(payment);
        
        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        assertEquals("txn_success_123", result.getTransactionId());
        assertTrue(result.isSuccessful());
    }

    @Test
    void processPayment_WithFailedPaymentStub_ShouldReturnFailedPayment() {
        // Arrange - Use failed payment stub
        paymentService = new PaymentService(new FailedPaymentGatewayStub());
        Payment payment = new Payment(100.0, "credit_card", "USD");
        
        // Act
        Payment result = paymentService.processPayment(payment);
        
        // Assert
        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
        assertNull(result.getTransactionId());
        assertFalse(result.isSuccessful());
        assertEquals("Insufficient funds", result.getErrorMessage());
    }
}
