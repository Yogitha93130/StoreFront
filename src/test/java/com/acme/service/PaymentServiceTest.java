package com.acme;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Stub implementations
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

public class PaymentServiceTest {

    @Test
    void processPayment_WithSuccessfulPaymentStub_ShouldReturnSuccessfulPayment() {
        // Arrange
        PaymentService paymentService = new PaymentService(new SuccessfulPaymentGatewayStub());
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
        // Arrange
        PaymentService paymentService = new PaymentService(new FailedPaymentGatewayStub());
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
