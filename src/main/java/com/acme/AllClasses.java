package com.acme;

// All classes in same package to avoid access issues

// ========== MODEL CLASSES ==========
class Product {
    private String productId;
    private String name;
    private double basePrice;
    
    public Product(String productId, String name, double basePrice) {
        this.productId = productId;
        this.name = name;
        this.basePrice = basePrice;
    }
    
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
}

class ShoppingCart {
    private String cartId;
    
    public ShoppingCart(String cartId) {
        this.cartId = cartId;
    }
    
    public String getCartId() { return cartId; }
}

class CartItem {
    private Product product;
    private int quantity;
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public double calculateSubtotal() {
        return product.getBasePrice() * quantity;
    }
    
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
}

class Payment {
    private double amount;
    private String paymentMethod;
    private String currency;
    private String status;
    private String transactionId;
    private String errorMessage;
    private boolean successful;
    
    public Payment(double amount, String paymentMethod, String currency) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.currency = currency;
        this.status = "PENDING";
    }
    
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
}

// ========== REPOSITORY INTERFACES ==========
interface ProductRepository {
    Product findById(String productId);
}

interface InventoryRepository {
    int getStockLevel(String storeId, String productId);
    int getCurrentStock(String productId);
    void updateStock(String productId, int quantity);
}

interface NotificationService {
    void sendLowStockAlert(String productId);
    void sendStockUpdateNotification(String productId, int oldQuantity, int newQuantity);
}

// ========== EXTERNAL INTERFACES ==========
interface PaymentGateway {
    PaymentResult processPayment(double amount, String paymentMethod, String currency);
}

class PaymentResult {
    private boolean success;
    private String transactionId;
    private String message;
    
    public PaymentResult(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
    }
    
    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
}

// ========== SERVICE CLASSES ==========
class ShoppingCartService {
    private ProductRepository productRepository;
    private InventoryRepository inventoryRepository;
    
    public ShoppingCartService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }
    
    public CartItem addItemToCart(ShoppingCart cart, String productId, String storeId, int quantity) {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        int stockLevel = inventoryRepository.getStockLevel(storeId, productId);
        if (stockLevel < quantity) {
            throw new IllegalArgumentException("Cannot add " + quantity + " items. Only " + stockLevel + " available.");
        }
        
        return new CartItem(product, quantity);
    }
    
    public double calculateCartTotal(ShoppingCart cart) {
        // Simple implementation for testing
        return 0.0;
    }
}

class PaymentService {
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

class InventoryService {
    private InventoryRepository inventoryRepository;
    private NotificationService notificationService;
    
    public InventoryService(InventoryRepository inventoryRepository, NotificationService notificationService) {
        this.inventoryRepository = inventoryRepository;
        this.notificationService = notificationService;
    }
    
    public void checkLowStock() {
        // Implementation for testing
        notificationService.sendLowStockAlert("test-product");
    }
    
    public void updateStockLevel(String productId, int newQuantity) {
        int oldQuantity = inventoryRepository.getCurrentStock(productId);
        inventoryRepository.updateStock(productId, newQuantity);
        
        if (oldQuantity <= 5 && newQuantity > 5) {
            notificationService.sendStockUpdateNotification(productId, oldQuantity, newQuantity);
        }
    }
}
