package com.fooddelivery.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a payment transaction in the Online Food Delivery System.
 */
public class Payment {

    public enum Method {
        CASH_ON_DELIVERY, CREDIT_CARD, DEBIT_CARD, UPI, WALLET
    }

    public enum Status {
        PENDING, SUCCESS, FAILED, REFUNDED
    }

    private String id;
    private String orderId;
    private double amount;
    private Method method;
    private Status status;
    private String transactionId;
    private String cardLastFour;     // Last 4 digits for display (masked)
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment(String orderId, double amount, Method method) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = Status.PENDING;
        this.transactionId = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with explicit ID (for testing)
    public Payment(String id, String orderId, double amount, Method method) {
        this(orderId, amount, method);
        this.id = id;
    }

    // Getters
    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public Method getMethod() { return method; }
    public Status getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public String getCardLastFour() { return cardLastFour; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters / State transitions
    public void markSuccess(String transactionId) {
        this.status = Status.SUCCESS;
        this.transactionId = transactionId;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {
        this.status = Status.FAILED;
        this.failureReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void markRefunded() {
        this.status = Status.REFUNDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCardLastFour(String lastFour) {
        this.cardLastFour = lastFour;
    }

    @Override
    public String toString() {
        return String.format("Payment{id='%s', orderId='%s', amount=%.2f, method=%s, status=%s}",
                id, orderId, amount, method, status);
    }
}
