package com.fooddelivery.model;

/**
 * Represents a single item entry in a shopping cart.
 * Pairs a MenuItem with a quantity.
 */
public class CartItem {

    private MenuItem menuItem;
    private int quantity;

    public CartItem(MenuItem menuItem, int quantity) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.quantity = quantity;
    }

    /**
     * Returns the subtotal for this cart item (price * quantity).
     */
    public double getSubtotal() {
        return Math.round(menuItem.getPrice() * quantity * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("CartItem{item='%s', qty=%d, subtotal=%.2f}",
                menuItem.getName(), quantity, getSubtotal());
    }
}
