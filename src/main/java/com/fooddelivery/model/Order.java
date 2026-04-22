package com.fooddelivery.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a food order in the Online Food Delivery System.
 * Tracks the order lifecycle from placement to delivery.
 */
public class Order {

    public enum Status {
        PLACED, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    }

    private String id;               // Format: ORD-XXXXXX
    private String userId;
    private String restaurantId;
    private List<CartItem> items;
    private double subtotal;
    private double tax;
    private double deliveryFee;
    private double discount;
    private double totalAmount;
    private Status status;
    private String paymentId;
    private String deliveryAddress;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order(String id, String userId, String restaurantId, List<CartItem> items,
                 double subtotal, double tax, double deliveryFee, double discount,
                 double totalAmount, String deliveryAddress) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = new ArrayList<>(items);
        this.subtotal = subtotal;
        this.tax = tax;
        this.deliveryFee = deliveryFee;
        this.discount = discount;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.status = Status.PLACED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getRestaurantId() { return restaurantId; }
    public List<CartItem> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getDiscount() { return discount; }
    public double getTotalAmount() { return totalAmount; }
    public Status getStatus() { return status; }
    public String getPaymentId() { return paymentId; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getCancellationReason() { return cancellationReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public void cancel(String reason) {
        this.status = Status.CANCELLED;
        this.cancellationReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isCancellable() {
        return status == Status.PLACED || status == Status.CONFIRMED;
    }

    @Override
    public String toString() {
        return String.format("Order{id='%s', userId='%s', total=%.2f, status=%s}",
                id, userId, totalAmount, status);
    }
}
