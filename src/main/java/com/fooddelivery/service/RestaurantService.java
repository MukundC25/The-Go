package com.fooddelivery.service;

import com.fooddelivery.exception.FoodDeliveryException;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.util.Validator;

import java.util.List;
import java.util.Optional;

/**
 * Service class for restaurant management operations.
 * Covers test cases: TC_RES_001–TC_RES_008
 */
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             OrderRepository orderRepository) {
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
    }

    // ── TC_RES_001: Add new menu item ────────────────────────────────────────

    /**
     * Adds a new menu item to a restaurant's menu.
     */
    public MenuItem addMenuItem(String restaurantId, String name, String description,
                                double price, String category) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        Validator.validateNotBlank(name, "item name");
        Validator.validatePrice(price);
        Validator.validateNotBlank(category, "category");

        MenuItem item = new MenuItem(restaurantId, name, description, price, category);
        restaurant.addMenuItem(item);
        restaurantRepository.save(restaurant);
        return item;
    }

    // ── TC_RES_002: Update menu item price ───────────────────────────────────

    /**
     * Updates the price of an existing menu item.
     */
    public MenuItem updateMenuItemPrice(String restaurantId, String itemId, double newPrice) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        Validator.validatePrice(newPrice);

        MenuItem item = restaurant.getMenu().stream()
                .filter(m -> m.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new FoodDeliveryException("ITEM_NOT_FOUND",
                        "Menu item not found: " + itemId));

        item.setPrice(newPrice);
        restaurantRepository.save(restaurant);
        return item;
    }

    // ── TC_RES_003: Mark item as out of stock ────────────────────────────────

    /**
     * Toggles the availability of a menu item (TC_RES_003).
     */
    public void markItemOutOfStock(String restaurantId, String itemId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        MenuItem item = findMenuItemInRestaurant(restaurant, itemId);
        item.setAvailable(false);
        restaurantRepository.save(restaurant);
    }

    public void markItemAvailable(String restaurantId, String itemId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        MenuItem item = findMenuItemInRestaurant(restaurant, itemId);
        item.setAvailable(true);
        restaurantRepository.save(restaurant);
    }

    // ── TC_RES_004: Accept incoming order ────────────────────────────────────

    /**
     * Accepts an incoming order (changes status PLACED -> CONFIRMED).
     */
    public Order acceptOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> FoodDeliveryException.orderNotFound(orderId));

        if (order.getStatus() != Order.Status.PLACED) {
            throw new FoodDeliveryException("INVALID_STATE",
                    "Order " + orderId + " is not in PLACED state");
        }
        order.setStatus(Order.Status.CONFIRMED);
        orderRepository.save(order);
        return order;
    }

    // ── TC_RES_005: Reject order with reason ─────────────────────────────────

    /**
     * Rejects an order with a reason (TC_RES_005).
     */
    public Order rejectOrder(String orderId, String reason) {
        Validator.validateNotBlank(reason, "rejection reason");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> FoodDeliveryException.orderNotFound(orderId));

        if (order.getStatus() != Order.Status.PLACED) {
            throw new FoodDeliveryException("INVALID_STATE",
                    "Order can only be rejected when PLACED");
        }
        order.cancel("Restaurant rejected: " + reason);
        orderRepository.save(order);
        return order;
    }

    // ── TC_RES_006: Update order preparation status ───────────────────────────

    /**
     * Updates order status from CONFIRMED to PREPARING.
     */
    public Order updateOrderStatus(String orderId, Order.Status newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> FoodDeliveryException.orderNotFound(orderId));
        order.setStatus(newStatus);
        orderRepository.save(order);
        return order;
    }

    // ── TC_RES_007: View order history ────────────────────────────────────────

    /**
     * Returns all orders for a specific restaurant.
     */
    public List<Order> getOrderHistory(String restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    // ── TC_RES_008: Generate sales report ────────────────────────────────────

    /**
     * Generates a simple sales summary for a restaurant.
     */
    public RestaurantSalesReport generateSalesReport(String restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        long delivered = orders.stream()
                .filter(o -> o.getStatus() == Order.Status.DELIVERED).count();
        double totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == Order.Status.DELIVERED)
                .mapToDouble(Order::getTotalAmount).sum();
        return new RestaurantSalesReport(restaurantId, orders.size(),
                (int) delivered, Math.round(totalRevenue * 100.0) / 100.0);
    }

    // ── Search & Browse ───────────────────────────────────────────────────────

    public List<Restaurant> searchRestaurants(String query) {
        return restaurantRepository.search(query);
    }

    public List<Restaurant> getOpenRestaurants() {
        return restaurantRepository.findOpen();
    }

    public List<Restaurant> getRestaurantsByCuisine(String cuisine) {
        return restaurantRepository.findByCuisine(cuisine);
    }

    public List<Restaurant> getRestaurantsByMinRating(double minRating) {
        return restaurantRepository.findByMinRating(minRating);
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    public Restaurant registerRestaurant(String ownerId, String name, String cuisine,
                                         String address, String phone) {
        Validator.validateNotBlank(name, "restaurant name");
        Validator.validateNotBlank(cuisine, "cuisine");
        Validator.validateNotBlank(address, "address");
        Validator.validatePhone(phone);

        Restaurant restaurant = new Restaurant(ownerId, name, cuisine, address, phone);
        restaurantRepository.save(restaurant);
        return restaurant;
    }

    public Restaurant approveRestaurant(String restaurantId) {
        Restaurant r = getRestaurantById(restaurantId);
        r.setApproved(true);
        r.setOpen(true);
        restaurantRepository.save(r);
        return r;
    }

    public Restaurant getRestaurantById(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> FoodDeliveryException.restaurantNotFound(restaurantId));
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    // ── Inner class for sales report ─────────────────────────────────────────

    private MenuItem findMenuItemInRestaurant(Restaurant restaurant, String itemId) {
        return restaurant.getMenu().stream()
                .filter(m -> m.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new FoodDeliveryException("ITEM_NOT_FOUND",
                        "Menu item not found: " + itemId));
    }

    public record RestaurantSalesReport(String restaurantId, int totalOrders,
                                        int deliveredOrders, double totalRevenue) {}
}
