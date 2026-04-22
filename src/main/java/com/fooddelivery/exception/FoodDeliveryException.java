package com.fooddelivery.exception;

/**
 * Base exception class for the Online Food Delivery System.
 * All domain-specific errors extend this class.
 */
public class FoodDeliveryException extends RuntimeException {

    private final String errorCode;

    public FoodDeliveryException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public FoodDeliveryException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // ── Common error factory methods ─────────────────────────────────────────

    public static FoodDeliveryException userNotFound(String identifier) {
        return new FoodDeliveryException("USER_NOT_FOUND",
                "User not found: " + identifier);
    }

    public static FoodDeliveryException emailAlreadyExists(String email) {
        return new FoodDeliveryException("EMAIL_EXISTS",
                "An account with email '" + email + "' already exists");
    }

    public static FoodDeliveryException invalidCredentials() {
        return new FoodDeliveryException("INVALID_CREDENTIALS",
                "Invalid email or password");
    }

    public static FoodDeliveryException accountLocked() {
        return new FoodDeliveryException("ACCOUNT_LOCKED",
                "Account is locked due to too many failed login attempts");
    }

    public static FoodDeliveryException invalidInput(String field, String reason) {
        return new FoodDeliveryException("INVALID_INPUT",
                "Invalid " + field + ": " + reason);
    }

    public static FoodDeliveryException cartEmpty() {
        return new FoodDeliveryException("CART_EMPTY",
                "Cart cannot be empty when placing an order");
    }

    public static FoodDeliveryException orderNotFound(String orderId) {
        return new FoodDeliveryException("ORDER_NOT_FOUND",
                "Order not found: " + orderId);
    }

    public static FoodDeliveryException orderNotCancellable(String orderId) {
        return new FoodDeliveryException("ORDER_NOT_CANCELLABLE",
                "Order " + orderId + " cannot be cancelled in its current status");
    }

    public static FoodDeliveryException restaurantClosed(String restaurantName) {
        return new FoodDeliveryException("RESTAURANT_CLOSED",
                "Restaurant '" + restaurantName + "' is currently closed");
    }

    public static FoodDeliveryException paymentFailed(String reason) {
        return new FoodDeliveryException("PAYMENT_FAILED",
                "Payment failed: " + reason);
    }

    public static FoodDeliveryException invalidPromoCode(String code) {
        return new FoodDeliveryException("INVALID_PROMO",
                "Promo code '" + code + "' is invalid or expired");
    }

    public static FoodDeliveryException restaurantNotFound(String id) {
        return new FoodDeliveryException("RESTAURANT_NOT_FOUND",
                "Restaurant not found: " + id);
    }

    public static FoodDeliveryException unauthorized(String action) {
        return new FoodDeliveryException("UNAUTHORIZED",
                "Not authorized to perform action: " + action);
    }

    @Override
    public String toString() {
        return String.format("FoodDeliveryException{code='%s', message='%s'}",
                errorCode, getMessage());
    }
}
