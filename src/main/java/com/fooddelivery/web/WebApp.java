package com.fooddelivery.web;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.*;
import com.fooddelivery.service.*;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

/**
 * Embedded Javalin web application for Online Food Delivery System.
 * Used by Selenium tests to test the full UI stack.
 *
 * Start: WebApp.start(port)
 * Stop:  WebApp.stop()
 */
public class WebApp {

    private static Javalin app;

    // Shared in-memory state
    private static final UserRepository userRepo       = new UserRepository();
    private static final RestaurantRepository restRepo = new RestaurantRepository();
    private static final OrderRepository orderRepo     = new OrderRepository();
    private static final PaymentRepository payRepo     = new PaymentRepository();

    private static final UserService       userService = new UserService(userRepo);
    private static final RestaurantService restService = new RestaurantService(restRepo, orderRepo);
    private static final CartService       cartService = new CartService(restRepo);
    private static final OrderService      orderService = new OrderService(orderRepo, payRepo, cartService);
    private static final PaymentService    payService  = new PaymentService(payRepo);

    public static synchronized void start(int port) {
        if (app != null) return; // Already running

        seedData();

        app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.staticFiles.add("/public", io.javalin.http.staticfiles.Location.CLASSPATH);
        });

        // ── Routes ──────────────────────────────────────────────────────────
        app.get("/",            WebApp::homePage);
        app.get("/register",    WebApp::registerPage);
        app.post("/register",   WebApp::handleRegister);
        app.get("/login",       WebApp::loginPage);
        app.post("/login",      WebApp::handleLogin);
        app.get("/logout",      WebApp::handleLogout);
        app.get("/forgot-password",  WebApp::forgotPasswordPage);
        app.post("/forgot-password", WebApp::handleForgotPassword);
        app.get("/reset-password",   WebApp::resetPasswordPage);
        app.post("/reset-password",  WebApp::handleResetPassword);
        app.get("/profile",     WebApp::profilePage);
        app.post("/profile",    WebApp::handleProfileUpdate);
        app.get("/restaurant/{id}", WebApp::restaurantPage);
        app.post("/cart/add",   WebApp::handleAddToCart);
        app.get("/cart",        WebApp::cartPage);
        app.post("/cart/remove",WebApp::handleRemoveFromCart);
        app.post("/cart/promo", WebApp::handleApplyPromo);
        app.get("/checkout",    WebApp::checkoutPage);
        app.post("/checkout",   WebApp::handleCheckout);
        app.get("/orders",      WebApp::ordersPage);
        app.get("/orders/{id}", WebApp::orderDetailPage);
        app.post("/orders/{id}/cancel", WebApp::handleCancelOrder);
        app.get("/admin",       WebApp::adminPage);

        app.start(port);
    }

    public static synchronized void stop() {
        if (app != null) {
            app.stop();
            app = null;
            userRepo.clear();
            restRepo.clear();
            orderRepo.clear();
            payRepo.clear();
        }
    }

    // ── Seed data for testing ────────────────────────────────────────────────

    private static void seedData() {
        // Seed admin user
        try {
            userService.registerWithRole("Admin User", "admin@foodapp.com",
                    "9876543210", "Admin@123!", User.Role.ADMIN);
        } catch (Exception ignored) {}

        // Seed restaurants
        Restaurant r1 = restService.registerRestaurant("owner1", "Tasty Bites",
                "Indian", "123 MG Road, Mumbai", "9876500001");
        restService.approveRestaurant(r1.getId());
        restService.addMenuItem(r1.getId(), "Butter Chicken", "Rich creamy curry", 280.0, "Main Course");
        restService.addMenuItem(r1.getId(), "Garlic Naan", "Soft leavened bread", 60.0, "Bread");
        restService.addMenuItem(r1.getId(), "Mango Lassi", "Sweet yogurt drink", 80.0, "Beverages");

        Restaurant r2 = restService.registerRestaurant("owner2", "Pizza Palace",
                "Italian", "456 Park Avenue, Delhi", "9876500002");
        restService.approveRestaurant(r2.getId());
        restService.addMenuItem(r2.getId(), "Margherita Pizza", "Classic tomato & cheese", 350.0, "Pizza");
        restService.addMenuItem(r2.getId(), "Pasta Arrabbiata", "Spicy tomato pasta", 250.0, "Pasta");

        Restaurant r3 = restService.registerRestaurant("owner3", "Burger Barn",
                "Fast Food", "789 High Street, Mumbai", "9876500003");
        restService.approveRestaurant(r3.getId());
        restService.addMenuItem(r3.getId(), "Classic Cheeseburger", "Juicy beef patty", 180.0, "Burgers");
        restService.addMenuItem(r3.getId(), "French Fries", "Crispy golden strings", 90.0, "Sides");

        Restaurant r4 = restService.registerRestaurant("owner4", "Sushi Symphony",
                "Japanese", "101 Ocean Drive, Mumbai", "9876500004");
        restService.approveRestaurant(r4.getId());
        restService.addMenuItem(r4.getId(), "Spicy Tuna Roll", "Fresh tuna with spicy mayo", 450.0, "Sushi");
        restService.addMenuItem(r4.getId(), "Miso Soup", "Traditional warm soup", 120.0, "Soups");

        Restaurant r5 = restService.registerRestaurant("owner5", "Sweet Tooth Desserts",
                "Desserts", "222 Baker Street, Pune", "9876500005");
        restService.approveRestaurant(r5.getId());
        restService.addMenuItem(r5.getId(), "Chocolate Lava Cake", "Gooey center cake", 220.0, "Desserts");
        restService.addMenuItem(r5.getId(), "Vanilla Bean Ice Cream", "Hand-churned scoop", 150.0, "Ice Cream");

        Restaurant r6 = restService.registerRestaurant("owner6", "The Daily Grind Cafe",
                "Cafe", "333 Brew Lane, Bangalore", "9876500006");
        restService.approveRestaurant(r6.getId());
        restService.addMenuItem(r6.getId(), "Cappuccino", "Rich espresso with steamed milk", 140.0, "Coffee");
        restService.addMenuItem(r6.getId(), "Avocado Toast", "Smashed avo on sourdough", 210.0, "Breakfast");
    }

    // ── Page Handlers ────────────────────────────────────────────────────────

    private static void homePage(Context ctx) {
        List<Restaurant> restaurants = restService.getOpenRestaurants();
        ctx.html(HtmlTemplates.home(restaurants, currentUser(ctx)));
    }

    private static void registerPage(Context ctx) {
        ctx.html(HtmlTemplates.register("", ""));
    }

    private static void handleRegister(Context ctx) {
        String name     = ctx.formParam("name");
        String email    = ctx.formParam("email");
        String phone    = ctx.formParam("phone");
        String password = ctx.formParam("password");
        String confirm  = ctx.formParam("confirmPassword");

        if (password != null && !password.equals(confirm)) {
            ctx.html(HtmlTemplates.register("Passwords do not match", email));
            return;
        }

        try {
            User user = userService.register(name, email, phone, password);
            ctx.sessionAttribute("userId", user.getId());
            ctx.redirect("/");
        } catch (Exception e) {
            ctx.html(HtmlTemplates.register(e.getMessage(), email));
        }
    }

    private static void loginPage(Context ctx) {
        String msg = ctx.queryParam("msg");
        ctx.html(HtmlTemplates.login(msg != null ? msg : "", ""));
    }

    private static void handleLogin(Context ctx) {
        String email    = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = userService.login(email, password);
            ctx.sessionAttribute("userId", user.getId());
            ctx.redirect("/");
        } catch (Exception e) {
            ctx.html(HtmlTemplates.login(e.getMessage(), email));
        }
    }

    private static void handleLogout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/login");
    }

    private static void restaurantPage(Context ctx) {
        String id = ctx.pathParam("id");
        try {
            Restaurant r = restService.getRestaurantById(id);
            ctx.html(HtmlTemplates.restaurant(r, currentUser(ctx)));
        } catch (Exception e) {
            ctx.html(HtmlTemplates.error("Restaurant not found"));
        }
    }

    private static void handleAddToCart(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login"); return; }

        try {
            String restaurantId = ctx.formParam("restaurantId");
            String itemId       = ctx.formParam("itemId");
            int    qty          = Integer.parseInt(ctx.formParam("quantity") != null
                    ? ctx.formParam("quantity") : "1");
            cartService.addItem(userId, restaurantId, itemId, qty);
            ctx.redirect("/cart");
        } catch (Exception e) {
            ctx.html(HtmlTemplates.error(e.getMessage()));
        }
    }

    private static void cartPage(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login"); return; }

        var cart  = cartService.getCart(userId);
        String msg = ctx.queryParam("msg");
        ctx.html(HtmlTemplates.cart(cart, currentUser(ctx), msg != null ? msg : ""));
    }

    private static void handleRemoveFromCart(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login"); return; }

        String itemId = ctx.formParam("itemId");
        try {
            cartService.removeItem(userId, itemId);
        } catch (Exception ignored) {}
        ctx.redirect("/cart");
    }

    private static void handleApplyPromo(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login"); return; }

        String code = ctx.formParam("promoCode");
        try {
            cartService.applyPromoCode(userId, code);
            ctx.redirect("/cart?msg=Promo+applied+successfully");
        } catch (Exception e) {
            ctx.redirect("/cart?msg=" + e.getMessage().replace(" ", "+"));
        }
    }

    private static void checkoutPage(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login"); return; }

        var cart = cartService.getCart(userId);
        if (cart.isEmpty()) { ctx.redirect("/cart?msg=Cart+is+empty"); return; }

        ctx.html(HtmlTemplates.checkout(cart, currentUser(ctx)));
    }

    private static void handleCheckout(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login"); return; }

        String restaurantId = ctx.formParam("restaurantId");
        String address      = ctx.formParam("address");
        String methodStr    = ctx.formParam("paymentMethod");

        try {
            Payment.Method method = switch (methodStr != null ? methodStr : "CASH_ON_DELIVERY") {
                case "UPI"              -> Payment.Method.UPI;
                case "CREDIT_CARD"      -> Payment.Method.CREDIT_CARD;
                case "DEBIT_CARD"       -> Payment.Method.DEBIT_CARD;
                case "WALLET"           -> Payment.Method.WALLET;
                default                  -> Payment.Method.CASH_ON_DELIVERY;
            };
            Order order = orderService.placeOrder(userId, restaurantId, address, method);

            if (method == Payment.Method.CASH_ON_DELIVERY) {
                payService.processCodPayment(order.getPaymentId());
            } else {
                // Simulate online payment success
                payService.processOnlinePayment(order.getPaymentId(), "TXN-" + System.currentTimeMillis());
            }

            ctx.redirect("/orders/" + order.getId());
        } catch (Exception e) {
            ctx.html(HtmlTemplates.error(e.getMessage()));
        }
    }

    private static void ordersPage(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login"); return; }

        List<Order> orders = orderService.getOrderHistory(userId);
        ctx.html(HtmlTemplates.orders(orders, currentUser(ctx)));
    }

    private static void orderDetailPage(Context ctx) {
        String userId  = currentUserId(ctx);
        String orderId = ctx.pathParam("id");
        if (userId == null) { ctx.redirect("/login"); return; }

        try {
            Order order = orderService.getOrderById(orderId);
            ctx.html(HtmlTemplates.orderDetail(order, currentUser(ctx)));
        } catch (Exception e) {
            ctx.html(HtmlTemplates.error("Order not found"));
        }
    }

    public static void main(String[] args) {
        start(7777);
    }

    private static void adminPage(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login?msg=Please+login"); return; }

        User user = userService.getUserById(userId);
        if (user.getRole() != User.Role.ADMIN) {
            ctx.html(HtmlTemplates.error("Access denied: Admins only"));
            return;
        }

        int totalUsers       = userService.getAllUsers().size();
        int totalRestaurants = restService.getAllRestaurants().size();
        int totalOrders      = orderService.getAllOrders().size();
        ctx.html(HtmlTemplates.admin(user, totalUsers, totalRestaurants, totalOrders));
    }

    // ── Session helpers ──────────────────────────────────────────────────────

    private static String currentUserId(Context ctx) {
        return ctx.sessionAttribute("userId");
    }

    private static User currentUser(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) return null;
        try {
            return userService.getUserById(userId);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Forgot / Reset Password ───────────────────────────────────────────────

    private static void forgotPasswordPage(Context ctx) {
        ctx.html(HtmlTemplates.forgotPassword(""));
    }

    private static void handleForgotPassword(Context ctx) {
        String email = ctx.formParam("email");
        try {
            String token = userService.initiatePasswordReset(email);
            // In production this token would be emailed; here we expose it for simulation
            ctx.html(HtmlTemplates.forgotPassword(
                "Reset link generated! Use token: " + token +
                " &mdash; <a href='/reset-password?token=" + token + "' class='font-bold underline'>Click here to reset</a>"));
        } catch (Exception e) {
            ctx.html(HtmlTemplates.forgotPassword("Error: " + e.getMessage()));
        }
    }

    private static void resetPasswordPage(Context ctx) {
        String token = ctx.queryParam("token");
        ctx.html(HtmlTemplates.resetPassword(token, ""));
    }

    private static void handleResetPassword(Context ctx) {
        String token       = ctx.formParam("token");
        String newPassword = ctx.formParam("newPassword");
        String confirmPwd  = ctx.formParam("confirmPassword");

        if (newPassword != null && !newPassword.equals(confirmPwd)) {
            ctx.html(HtmlTemplates.resetPassword(token, "Passwords do not match"));
            return;
        }
        try {
            userService.completePasswordReset(token, newPassword);
            ctx.redirect("/login?msg=Password+reset+successfully");
        } catch (Exception e) {
            ctx.html(HtmlTemplates.resetPassword(token, e.getMessage()));
        }
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    private static void profilePage(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login"); return; }
        ctx.html(HtmlTemplates.profile(userService.getUserById(userId), ""));
    }

    private static void handleProfileUpdate(Context ctx) {
        String userId = currentUserId(ctx);
        if (userId == null) { ctx.redirect("/login"); return; }
        try {
            User updated = userService.updateProfile(userId, ctx.formParam("name"), ctx.formParam("phone"));
            ctx.html(HtmlTemplates.profile(updated, "Profile updated successfully!"));
        } catch (Exception e) {
            ctx.html(HtmlTemplates.profile(userService.getUserById(userId), "Error: " + e.getMessage()));
        }
    }

    // ── Cancel Order ─────────────────────────────────────────────────────────

    private static void handleCancelOrder(Context ctx) {
        String userId  = currentUserId(ctx);
        String orderId = ctx.pathParam("id");
        if (userId == null) { ctx.redirect("/login"); return; }
        try {
            orderService.cancelOrder(orderId, userId);
            payService.refundPayment(orderService.getOrderById(orderId).getPaymentId());
        } catch (Exception ignored) {}
        ctx.redirect("/orders/" + orderId);
    }
}

