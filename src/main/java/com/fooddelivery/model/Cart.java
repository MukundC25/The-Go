package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a user's shopping cart in the Online Food Delivery System.
 * Supports items, promo codes, tax calculation, and total computation.
 */
public class Cart {

    public static final double TAX_RATE = 0.05;       // 5% GST
    public static final double DELIVERY_FEE = 30.0;   // Flat delivery fee

    private String userId;
    private List<CartItem> items;
    private String promoCode;
    private double discount;

    public Cart(String userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
        this.promoCode = null;
        this.discount = 0.0;
    }

    // Getters
    public String getUserId() { return userId; }
    public List<CartItem> getItems() { return items; }
    public String getPromoCode() { return promoCode; }
    public double getDiscount() { return discount; }

    public boolean isEmpty() { return items.isEmpty(); }

    /**
     * Adds item to cart. If item already exists, increases quantity.
     */
    public void addItem(MenuItem menuItem, int quantity) {
        Optional<CartItem> existing = items.stream()
                .filter(ci -> ci.getMenuItem().getId().equals(menuItem.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            items.add(new CartItem(menuItem, quantity));
        }
    }

    /**
     * Removes a menu item from the cart by its ID.
     */
    public boolean removeItem(String menuItemId) {
        return items.removeIf(ci -> ci.getMenuItem().getId().equals(menuItemId));
    }

    /**
     * Updates quantity of a specific item. Removes item if quantity drops to 0.
     */
    public boolean updateItemQuantity(String menuItemId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeItem(menuItemId);
        }
        Optional<CartItem> found = items.stream()
                .filter(ci -> ci.getMenuItem().getId().equals(menuItemId))
                .findFirst();
        if (found.isPresent()) {
            found.get().setQuantity(newQuantity);
            return true;
        }
        return false;
    }

    /**
     * Returns the subtotal before tax and delivery (after discount).
     */
    public double getSubtotal() {
        double raw = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        return Math.round((raw - discount) * 100.0) / 100.0;
    }

    /**
     * Returns the tax amount (5% on subtotal).
     */
    public double getTax() {
        return Math.round(getSubtotal() * TAX_RATE * 100.0) / 100.0;
    }

    /**
     * Returns the grand total (subtotal + tax + delivery fee).
     */
    public double getGrandTotal() {
        if (items.isEmpty()) return 0.0;
        return Math.round((getSubtotal() + getTax() + DELIVERY_FEE) * 100.0) / 100.0;
    }

    public void applyPromoCode(String code, double discountAmount) {
        this.promoCode = code;
        this.discount = discountAmount;
    }

    public void removePromoCode() {
        this.promoCode = null;
        this.discount = 0.0;
    }

    public void clear() {
        items.clear();
        promoCode = null;
        discount = 0.0;
    }

    @Override
    public String toString() {
        return String.format("Cart{userId='%s', items=%d, total=%.2f}",
                userId, items.size(), getGrandTotal());
    }
}
