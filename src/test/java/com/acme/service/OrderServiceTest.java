package com.acme.service;

import com.acme.model.*;
import com.acme.repository.OrderRepository;
import com.acme.external.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Stub implementation for testing
class TestPaymentGatewayStub implements PaymentGateway {
    private boolean shouldSucceed;
    
    public TestPaymentGatewayStub(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        if (shouldSucceed) {
            return new PaymentResult(true, "txn-12345", "Payment processed successfully");
        } else {
            return new PaymentResult(false, null, "Insufficient funds");
        }
    }
    
    @Override
    public boolean validatePaymentMethod(String paymentMethodId) {
        return paymentMethodId != null && !paymentMethodId.trim().isEmpty();
    }
}

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    private OrderService orderService;
    private ShoppingCart testCart;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("user-123");
        testUser.setEmail("alex@example.com");
        
        testCart = new ShoppingCart();
        testCart.setCartId("cart-123");
        
        Product product1 = new Product();
        product1.setProductId("prod-123");
        product1.setName("Nike Air Max");
        product1.setBasePrice(129.99);
        
        Product product2 = new Product();
        product2.setProductId("prod-456");
        product2.setName("Running Socks");
        product2.setBasePrice(12.99);
        
        CartItem item1 = new CartItem(testCart, product1, 1);
        CartItem item2 = new CartItem(testCart, product2, 2);
        testCart.getCartItems().addAll(Arrays.asList(item1, item2));
    }

    @Test
    void processOrder_WithSuccessfulPaymentStub_ShouldCreateOrder() {
        // Arrange - Using stub for successful payment
        PaymentGateway paymentGatewayStub = new TestPaymentGatewayStub(true);
        orderService = new OrderService(orderRepository, paymentGatewayStub);
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId("order-123");
            return order;
        });
        
        // Act
        Order result = orderService.processOrder(testUser, testCart, "pay-method-123", "store-1");
        
        // Assert
        assertNotNull(result);
        assertEquals("order-123", result.getOrderId());
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(155.97, result.getTotalAmount(), 0.01); // 129.99 + (12.99 * 2)
        assertEquals("txn-12345", result.getPayment().getTransactionId());
        
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void processOrder_WithFailedPaymentStub_ShouldThrowException() {
        // Arrange - Using stub for failed payment
        PaymentGateway paymentGatewayStub = new TestPaymentGatewayStub(false);
        orderService = new OrderService(orderRepository, paymentGatewayStub);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> orderService.processOrder(testUser, testCart, "pay-method-123", "store-1"));
        
        assertEquals("Payment failed: Insufficient funds", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }
}
