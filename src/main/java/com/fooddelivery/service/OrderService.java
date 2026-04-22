package com.fooddelivery.service;

import com.fooddelivery.exception.FoodDeliveryException;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Payment;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.PaymentRepository;

import java.util.List;

/**
 * Service class for order management operations.
 * Covers test cases: TC_ORD_007–TC_ORD_010, RT_001–RT_005
 */
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        PaymentRepository paymentRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.cartService = cartService;
    }

    // ── TC_ORD_007 & TC_ORD_008: Place order (COD or Online) ─────────────────

    /**
     * Places an order from the user's current cart.
     *
     * @param userId          The customer's user ID
     * @param restaurantId    The restaurant being ordered from
     * @param deliveryAddress Delivery address
     * @param paymentMethod   COD, CARD, UPI, or WALLET
     * @return The created Order
     * @throws FoodDeliveryException if cart is empty (RT_006)
     */
    public Order placeOrder(String userId, String restaurantId, String deliveryAddress,
                            Payment.Method paymentMethod) {
        // RT_006: Validate cart is not empty
        cartService.validateCartNotEmpty(userId);

        Cart cart = cartService.getCart(userId);

        String orderId = orderRepository.generateOrderId();
        Order order = new Order(
                orderId, userId, restaurantId,
                cart.getItems(),
                cart.getSubtotal(),
                cart.getTax(),
                Cart.DELIVERY_FEE,
                cart.getDiscount(),
                cart.getGrandTotal(),
                deliveryAddress
        );
        orderRepository.save(order);

        // Create a pending payment record
        Payment payment = new Payment(orderId, cart.getGrandTotal(), paymentMethod);
        paymentRepository.save(payment);
        order.setPaymentId(payment.getId());
        orderRepository.save(order);

        // Clear the cart after successful order placement
        cartService.clearCart(userId);

        return order;
    }

    // ── TC_ORD_009: Cancel order before confirmation ──────────────────────────

    /**
     * Cancels an order. Only allowed when status is PLACED or CONFIRMED.
     * TC_ORD_009 → before confirmation
     * TC_ORD_010 → after confirmation (should also be allowed if still CONFIRMED)
     */
    public Order cancelOrder(String orderId, String reason) {
        Order order = getOrderById(orderId);

        if (!order.isCancellable()) {
            throw FoodDeliveryException.orderNotCancellable(orderId);
        }

        order.cancel(reason != null ? reason : "Cancelled by customer");
        orderRepository.save(order);

        // Trigger refund if payment was made
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            if (payment.getStatus() == Payment.Status.SUCCESS) {
                payment.markRefunded();
                paymentRepository.save(payment);
            }
        });

        return order;
    }

    // ── Order status updates ──────────────────────────────────────────────────

    public Order updateOrderStatus(String orderId, Order.Status newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus);
        orderRepository.save(order);
        return order;
    }

    // ── History & Lookup ──────────────────────────────────────────────────────

    /**
     * TC_RES_007, FT_001 — Returns order history for a user.
     */
    public List<Order> getOrderHistory(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> FoodDeliveryException.orderNotFound(orderId));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatus(Order.Status status) {
        return orderRepository.findByStatus(status);
    }

    // ── RT_004: Concurrent order modification guard ───────────────────────────

    /**
     * Checks if an order is in a specific expected status before modifying.
     * Prevents race conditions in concurrent update scenarios.
     */
    public synchronized Order updateOrderStatusSafe(String orderId,
                                                     Order.Status expectedStatus,
                                                     Order.Status newStatus) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != expectedStatus) {
            throw new FoodDeliveryException("CONCURRENT_MODIFICATION",
                    "Order state has changed. Expected: " + expectedStatus
                            + ", Actual: " + order.getStatus());
        }
        order.setStatus(newStatus);
        orderRepository.save(order);
        return order;
    }
}
