package com.fooddelivery.service;

import com.fooddelivery.exception.FoodDeliveryException;
import com.fooddelivery.model.Payment;
import com.fooddelivery.repository.PaymentRepository;

import java.util.UUID;

/**
 * Service class for payment processing in the Online Food Delivery System.
 * Covers test cases: RT_007 (expired card), TC_SEC_006 (data protection)
 */
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // ── Process payment ───────────────────────────────────────────────────────

    /**
     * Processes an online payment for an order.
     * Handles: credit/debit card, UPI, wallet.
     *
     * @param paymentId  The payment record ID (created during order placement)
     * @param cardNumber Card number (if card payment) — masked after processing
     * @param expiry     Card expiry in MM/YY format (validated for expiration)
     * @param cvv        CVV (validated but never stored)
     * @return Updated Payment with SUCCESS status
     */
    public Payment processCardPayment(String paymentId, String cardNumber,
                                      String expiry, String cvv) {
        Payment payment = getPaymentById(paymentId);

        if (payment.getStatus() == Payment.Status.SUCCESS) {
            throw new FoodDeliveryException("DUPLICATE_PAYMENT",
                    "Payment has already been processed for this order");
        }

        // Validate card details
        validateCard(cardNumber, expiry, cvv);

        // Store only last 4 digits (SEC_006: sensitive data protection)
        String lastFour = cardNumber.replaceAll("\\s", "")
                .substring(cardNumber.replaceAll("\\s", "").length() - 4);
        payment.setCardLastFour(lastFour);

        // Simulate payment gateway (always succeeds in test environment)
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.markSuccess(transactionId);
        paymentRepository.save(payment);
        return payment;
    }

    /**
     * Process COD payment — marks payment as SUCCESS without card validation.
     */
    public Payment processCodPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (payment.getStatus() == Payment.Status.SUCCESS) {
            throw new FoodDeliveryException("DUPLICATE_PAYMENT",
                    "Payment is already recorded for this order");
        }

        if (payment.getMethod() != Payment.Method.CASH_ON_DELIVERY) {
            throw new FoodDeliveryException("INVALID_METHOD",
                    "This payment method is not Cash on Delivery");
        }

        String transactionId = "COD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.markSuccess(transactionId);
        paymentRepository.save(payment);
        return payment;
    }

    /**
     * Simulate online payment success for UPI, Card, Wallet, Net Banking.
     * In production, this would call the payment gateway API.
     */
    public Payment processOnlinePayment(String paymentId, String transactionId) {
        Payment payment = getPaymentById(paymentId);
        if (payment.getStatus() == Payment.Status.SUCCESS) {
            return payment; // Idempotent
        }
        payment.markSuccess(transactionId);
        paymentRepository.save(payment);
        return payment;
    }


    /**
     * Issues a refund for a payment.
     */
    public Payment refundPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (payment.getStatus() != Payment.Status.SUCCESS) {
            throw new FoodDeliveryException("INVALID_REFUND",
                    "Only successful payments can be refunded");
        }

        payment.markRefunded();
        paymentRepository.save(payment);
        return payment;
    }

    // ── Card Validation ───────────────────────────────────────────────────────

    /**
     * Validates card number (Luhn check), expiry (not expired), and CVV format.
     * RT_007: Expired card validation.
     *
     * @throws FoodDeliveryException if any validation fails
     */
    public void validateCard(String cardNumber, String expiry, String cvv) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw FoodDeliveryException.invalidInput("card number", "Card number is required");
        }

        String cleanCard = cardNumber.replaceAll("[\\s-]", "");
        if (cleanCard.length() < 13 || cleanCard.length() > 19) {
            throw FoodDeliveryException.invalidInput("card number",
                    "Card number must be 13-19 digits");
        }
        if (!cleanCard.matches("\\d+")) {
            throw FoodDeliveryException.invalidInput("card number",
                    "Card number must contain only digits");
        }
        if (!luhnCheck(cleanCard)) {
            throw FoodDeliveryException.invalidInput("card number",
                    "Invalid card number (failed Luhn check)");
        }

        // RT_007: Validate expiry
        validateExpiry(expiry);

        // Validate CVV
        if (cvv == null || !cvv.matches("^\\d{3,4}$")) {
            throw FoodDeliveryException.invalidInput("CVV", "CVV must be 3 or 4 digits");
        }
    }

    /**
     * RT_007 — Validates card expiry. Throws if card is expired.
     */
    public void validateExpiry(String expiry) {
        if (expiry == null || !expiry.matches("^\\d{2}/\\d{2}$")) {
            throw FoodDeliveryException.invalidInput("expiry",
                    "Expiry must be in MM/YY format");
        }

        String[] parts = expiry.split("/");
        int month = Integer.parseInt(parts[0]);
        int year  = Integer.parseInt(parts[1]) + 2000; // 2-digit year

        if (month < 1 || month > 12) {
            throw FoodDeliveryException.invalidInput("expiry", "Invalid month in expiry");
        }

        java.time.YearMonth expireDate = java.time.YearMonth.of(year, month);
        java.time.YearMonth now = java.time.YearMonth.now();

        if (expireDate.isBefore(now)) {
            throw FoodDeliveryException.paymentFailed("Card has expired (expiry: " + expiry + ")");
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new FoodDeliveryException("PAYMENT_NOT_FOUND",
                        "Payment not found: " + paymentId));
    }

    public Payment getPaymentStatus(String paymentId) {
        return getPaymentById(paymentId);
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new FoodDeliveryException("PAYMENT_NOT_FOUND",
                        "No payment found for order: " + orderId));
    }

    // ── Luhn Algorithm ────────────────────────────────────────────────────────

    private boolean luhnCheck(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }
}
