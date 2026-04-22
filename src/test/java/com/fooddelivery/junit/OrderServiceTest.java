package com.fooddelivery.junit;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.Payment;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.PaymentRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.service.CartService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.exception.FoodDeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceTest {

    private OrderRepository orderRepo;
    private PaymentRepository paymentRepo;
    private RestaurantRepository restRepo;
    private CartService cartService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepo = new OrderRepository();
        paymentRepo = new PaymentRepository();
        restRepo = new RestaurantRepository();
        cartService = new CartService(restRepo);
        orderService = new OrderService(orderRepo, paymentRepo, cartService);
    }

    @Test
    @DisplayName("TC_ORD_007: Place order with cash on delivery")
    void placeOrderWithCOD() {
        restaurantSetup();
        cartService.addItem("user1", "rest1", "item1", 2);

        Order order = orderService.placeOrder("user1", "rest1", "123 Street", Payment.Method.CASH_ON_DELIVERY);

        assertThat(order).isNotNull();
        assertThat(order.getUserId()).isEqualTo("user1");
        assertThat(order.getTotalAmount()).isGreaterThan(0.0);
        assertThat(order.getStatus()).isEqualTo(Order.Status.PLACED);
    }

    @Test
    @DisplayName("RT_006: Order with 0 items (Empty cart checkout)")
    void emptyCartCheckoutBlocked() {
        assertThatThrownBy(() -> orderService.placeOrder("user1", "rest1", "123 Street", Payment.Method.CASH_ON_DELIVERY))
                .isInstanceOf(FoodDeliveryException.class);
    }

    @Test
    @DisplayName("TC_ORD_009: Order cancellation before confirmation")
    void orderCancellationBeforeConfirmation() {
        restaurantSetup();
        cartService.addItem("user1", "rest1", "item1", 1);
        Order order = orderService.placeOrder("user1", "rest1", "123", Payment.Method.UPI);

        orderService.updateOrderStatus(order.getId(), Order.Status.CANCELLED);
        Order fetched = orderService.getOrderById(order.getId());
        assertThat(fetched.getStatus()).isEqualTo(Order.Status.CANCELLED);
    }

    @Test
    @DisplayName("Order history retrieval")
    void getOrderHistory() {
        restaurantSetup();
        cartService.addItem("user1", "rest1", "item1", 1);
        orderService.placeOrder("user1", "rest1", "123", Payment.Method.UPI);

        List<Order> history = orderService.getOrderHistory("user1");
        assertThat(history).hasSize(1);
    }

    private void restaurantSetup() {
        Restaurant r = new Restaurant("rest1", "ownerId", "Test Rest", "Italian", "Address", "123");
        restRepo.save(r);
        r.setOpen(true);
        com.fooddelivery.model.MenuItem item = new com.fooddelivery.model.MenuItem("item1", "rest1", "Pizza", "Desc", 100.0, "Category");
        r.addMenuItem(item);
    }
}
