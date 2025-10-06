package com.acme.model;

public class Payment {
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
    
    // Getters and setters
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
}
