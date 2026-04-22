package com.fooddelivery.repository;

import com.fooddelivery.model.Order;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory repository for Order entities.
 * Generates sequential order IDs in format ORD-000001.
 */
public class OrderRepository {

    private final Map<String, Order> store = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1);

    public String generateOrderId() {
        return String.format("ORD-%06d", counter.getAndIncrement());
    }

    public void save(Order order) {
        store.put(order.getId(), order);
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Order> findByUserId(String userId) {
        return store.values().stream()
                .filter(o -> o.getUserId().equals(userId))
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Order> findByRestaurantId(String restaurantId) {
        return store.values().stream()
                .filter(o -> o.getRestaurantId().equals(restaurantId))
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Order> findByStatus(Order.Status status) {
        return store.values().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Order> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
        counter.set(1);
    }
}
