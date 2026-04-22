package com.fooddelivery.service;

import com.fooddelivery.exception.FoodDeliveryException;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.util.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for shopping cart management.
 * Covers test cases: TC_ORD_001–TC_ORD_006, RT_006
 */
public class CartService {

    /** Valid promo codes: code → discount amount (in rupees) */
    private static final Map<String, Double> VALID_PROMO_CODES = new HashMap<>(Map.of(
            "SAVE50",   50.0,
            "FIRST100", 100.0,
            "FLAT20",   20.0,
            "WELCOME",  75.0
    ));

    private final Map<String, Cart> carts = new ConcurrentHashMap<>();
    private final RestaurantRepository restaurantRepository;

    public CartService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Returns or creates a cart for the given user.
     */
    public Cart getOrCreateCart(String userId) {
        return carts.computeIfAbsent(userId, Cart::new);
    }

    // ── TC_ORD_001: Add item to cart ─────────────────────────────────────────

    /**
     * Adds a menu item to the user's cart.
     *
     * @throws FoodDeliveryException if item is unavailable or restaurant is closed
     */
    public Cart addItem(String userId, String restaurantId, String menuItemId, int quantity) {
        Validator.validateQuantity(quantity);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> FoodDeliveryException.restaurantNotFound(restaurantId));

        if (!restaurant.isOpen()) {
            throw FoodDeliveryException.restaurantClosed(restaurant.getName());
        }

        MenuItem item = restaurant.getMenu().stream()
                .filter(m -> m.getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(() -> new FoodDeliveryException("ITEM_NOT_FOUND",
                        "Menu item not found: " + menuItemId));

        if (!item.isAvailable()) {
            throw new FoodDeliveryException("ITEM_UNAVAILABLE",
                    "'" + item.getName() + "' is currently out of stock");
        }

        Cart cart = getOrCreateCart(userId);
        cart.addItem(item, quantity);
        return cart;
    }

    // ── TC_ORD_002: Update item quantity ──────────────────────────────────────

    /**
     * Updates the quantity of an item already in the cart.
     */
    public Cart updateItemQuantity(String userId, String menuItemId, int newQuantity) {
        if (newQuantity != 0) {
            Validator.validateQuantity(newQuantity);
        }
        Cart cart = getOrCreateCart(userId);
        boolean updated = cart.updateItemQuantity(menuItemId, newQuantity);
        if (!updated) {
            throw new FoodDeliveryException("ITEM_NOT_IN_CART",
                    "Item " + menuItemId + " is not in the cart");
        }
        return cart;
    }

    // ── TC_ORD_003: Remove item from cart ────────────────────────────────────

    /**
     * Removes an item from the user's cart.
     */
    public Cart removeItem(String userId, String menuItemId) {
        Cart cart = getOrCreateCart(userId);
        boolean removed = cart.removeItem(menuItemId);
        if (!removed) {
            throw new FoodDeliveryException("ITEM_NOT_IN_CART",
                    "Item " + menuItemId + " is not in the cart");
        }
        return cart;
    }

    // ── TC_ORD_004: Apply valid promo code ────────────────────────────────────

    /**
     * Applies a promo code to the cart.
     *
     * @throws FoodDeliveryException for invalid/expired codes (TC_ORD_005)
     */
    public Cart applyPromoCode(String userId, String code) {
        Validator.validateNotBlank(code, "promo code");
        String upperCode = code.trim().toUpperCase();

        if (!VALID_PROMO_CODES.containsKey(upperCode)) {
            throw FoodDeliveryException.invalidPromoCode(code);   // TC_ORD_005
        }

        Cart cart = getOrCreateCart(userId);
        if (cart.isEmpty()) {
            throw FoodDeliveryException.cartEmpty();
        }

        double discount = VALID_PROMO_CODES.get(upperCode);
        cart.applyPromoCode(upperCode, discount);
        return cart;
    }

    // ── TC_ORD_006: Calculate order total with taxes ──────────────────────────

    /**
     * Returns the cart, which provides getGrandTotal() = subtotal + tax + delivery.
     */
    public Cart getCart(String userId) {
        return getOrCreateCart(userId);
    }

    /**
     * Clears the user's cart (called after successful order placement).
     */
    public void clearCart(String userId) {
        Cart cart = carts.get(userId);
        if (cart != null) {
            cart.clear();
        }
    }

    /**
     * RT_006: Validates that the cart is non-empty before placing an order.
     *
     * @throws FoodDeliveryException if cart is empty
     */
    public void validateCartNotEmpty(String userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.isEmpty()) {
            throw FoodDeliveryException.cartEmpty();
        }
    }
}
