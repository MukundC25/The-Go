package com.fooddelivery.repository;

import com.fooddelivery.model.Payment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for Payment entities.
 */
public class PaymentRepository {

    private final Map<String, Payment> store = new ConcurrentHashMap<>();

    public void save(Payment payment) {
        store.put(payment.getId(), payment);
    }

    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Payment> findByOrderId(String orderId) {
        return store.values().stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .findFirst();
    }

    public List<Payment> findByStatus(Payment.Status status) {
        return store.values().stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    public boolean existsByOrderId(String orderId) {
        return store.values().stream()
                .anyMatch(p -> p.getOrderId().equals(orderId) &&
                        p.getStatus() == Payment.Status.SUCCESS);
    }

    public List<Payment> findAll() {
        return new ArrayList<>(store.values());
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
