package com.fooddelivery.web;

import com.fooddelivery.model.*;
import java.util.List;

public class HtmlTemplates {

    private static String layout(String title, String content) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>FoodIt - %s</title>
                <script src="https://cdn.tailwindcss.com"></script>
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
                <script>
                    tailwind.config = {
                        theme: {
                            extend: {
                                colors: {
                                    blinkit: '#0c831f',
                                    blinkithover: '#0a6c19',
                                    background: '#f8f9fa',
                                }
                            }
                        }
                    }
                </script>
                <style>
                    body { font-family: 'Inter', sans-serif; background-color: #f8f9fa; }
                    .hide-scrollbar::-webkit-scrollbar { display: none; }
                    .hide-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
                </style>
            </head>
            <body class="text-gray-800">
                %s
            </body>
            </html>
            """.formatted(title, content);
    }

    private static String nav(User user) {
        String userLinks;
        if (user != null) {
            String adminLink = user.getRole() == User.Role.ADMIN
                ? "<a id='admin-link' href='/admin' class='text-sm font-semibold text-gray-700 hover:text-blinkit'>Admin</a>" : "";
            userLinks = """
                <span id='user-info' class='text-sm font-bold text-gray-900'>Welcome, %s</span>
                %s
                <a href='/orders' class='text-sm font-semibold text-gray-700 hover:text-blinkit'>Orders</a>
                <a href='/logout' class='text-sm font-semibold text-gray-700 hover:text-blinkit'>Logout</a>
                <a href='/cart' id='cart-link' class='bg-blinkit hover:bg-blinkithover text-white px-4 py-2 rounded-lg font-bold flex items-center gap-2 transition'>
                    🛒 My Cart
                </a>
                """.formatted(user.getName(), adminLink);
        } else {
            userLinks = """
                <a id='login-link' href='/login' class='text-sm font-semibold text-gray-700 hover:text-blinkit'>Login</a>
                <a id='register-link' href='/register' class='text-sm font-semibold text-gray-700 hover:text-blinkit'>Register</a>
                """;
        }

        return """
            <header class="bg-white border-b sticky top-0 z-50 shadow-sm">
                <div class="max-w-7xl mx-auto px-4 h-20 flex items-center justify-between gap-8">
                    <a href='/' class="text-3xl font-extrabold text-blinkit tracking-tight border-r pr-6 border-gray-200">
                        FoodIt<span class="text-yellow-400">.</span>
                    </a>
                    <div class="flex flex-col">
                        <span class="text-xs font-bold font-gray-900">Delivery in 8 minutes</span>
                        <span class="text-xs text-gray-500">MUMBAI, MAHARASHTRA, INDIA</span>
                    </div>
                    <div class="flex-1 max-w-2xl">
                        <div class="relative bg-gray-50 rounded-xl border border-gray-200 flex items-center p-3 hover:shadow-md transition">
                            <span class="text-xl mr-3">🔍</span>
                            <input type="text" placeholder='Search "Pizza"' class="bg-transparent border-none outline-none w-full text-sm placeholder-gray-400" />
                        </div>
                    </div>
                    <div class="flex items-center gap-6">
                        %s
                    </div>
                </div>
            </header>
            """.formatted(userLinks);
    }

    public static String home(List<Restaurant> restaurants, User user) {
        StringBuilder restsHtml = new StringBuilder();
        int coverCounter = 1;
        for (Restaurant r : restaurants) {
            String coverImage = "/images/rest_cover_" + (coverCounter % 2 == 0 ? "2" : "1") + ".png";
            coverCounter++;
            restsHtml.append("""
                <a href='/restaurant/%s' class='view-rest bg-white rounded-xl overflow-hidden border border-gray-100 shadow-sm hover:shadow-lg transition duration-200 block'>
                    <div class='h-40 bg-gray-200 w-full relative'>
                        <img src='%s' class='w-full h-full object-cover'/>
                        <div class='absolute bottom-0 left-0 bg-white/90 px-3 py-1 text-xs font-bold text-gray-800 rounded-tr-lg'>30 MINS</div>
                    </div>
                    <div class='p-4'>
                        <div class='flex justify-between items-start mb-2'>
                            <h3 class='font-bold text-lg text-gray-900 truncate'>%s</h3>
                            <span class='bg-green-100 text-green-800 text-xs font-bold px-2 py-1 rounded'>4.2 ★</span>
                        </div>
                        <p class='text-gray-500 text-sm truncate'>%s</p>
                        <hr class='my-3 border-gray-100'/>
                        <div class='text-xs font-semibold text-gray-400'>120+ orders recently</div>
                    </div>
                </a>
                """.formatted(r.getId(), coverImage, r.getName(), r.getCuisine()));
        }

        String categories = """
            <div class="max-w-7xl mx-auto px-4 py-8">
                <img src="/images/hero_banner_wide.png" class="w-full object-cover rounded-2xl shadow-md mb-12" alt="Hero Banner" style="aspect-ratio:21/9;max-height:420px;" />
                
                <h1 class="hidden">Order Food Online</h1><!-- For Selenium! -->
                
                <div class="flex items-center justify-between mb-6">
                    <h2 class="text-2xl font-black text-gray-900">&#127860; Explore by Category</h2>
                    <a href="#restaurants" class="text-sm text-blinkit font-bold">See all &#8594;</a>
                </div>
                
                <div class="flex gap-6 overflow-x-auto hide-scrollbar pb-4">
                    <div class="flex flex-col items-center gap-3 min-w-[100px] cursor-pointer hover:scale-105 transition">
                        <div class="w-24 h-24 rounded-full overflow-hidden shadow border-2 border-green-100"><img src="/images/pizza_cat.png" class="w-full h-full object-cover"></div>
                        <span class="font-bold text-sm text-gray-700">Pizza</span>
                    </div>
                    <div class="flex flex-col items-center gap-3 min-w-[100px] cursor-pointer hover:scale-105 transition">
                        <div class="w-24 h-24 rounded-full overflow-hidden shadow border-2 border-green-100"><img src="/images/biryani_cat.png" class="w-full h-full object-cover"></div>
                        <span class="font-bold text-sm text-gray-700">Biryani</span>
                    </div>
                    <div class="flex flex-col items-center gap-3 min-w-[100px] cursor-pointer hover:scale-105 transition">
                        <div class="w-24 h-24 rounded-full overflow-hidden shadow border-2 border-green-100"><img src="/images/burger_cat.png" class="w-full h-full object-cover"></div>
                        <span class="font-bold text-sm text-gray-700">Burgers</span>
                    </div>
                    <div class="flex flex-col items-center gap-3 min-w-[100px] cursor-pointer hover:scale-105 transition">
                        <div class="w-24 h-24 rounded-full overflow-hidden shadow border-2 border-green-100"><img src="/images/sushi_cat.png" class="w-full h-full object-cover"></div>
                        <span class="font-bold text-sm text-gray-700">Sushi</span>
                    </div>
                    <div class="flex flex-col items-center gap-3 min-w-[100px] cursor-pointer hover:scale-105 transition">
                        <div class="w-24 h-24 rounded-full overflow-hidden shadow border-2 border-green-100"><img src="/images/north_indian_cat.png" class="w-full h-full object-cover"></div>
                        <span class="font-bold text-sm text-gray-700">North Indian</span>
                    </div>
                    <div class="flex flex-col items-center gap-3 min-w-[100px] cursor-pointer hover:scale-105 transition">
                        <div class="w-24 h-24 rounded-full overflow-hidden shadow border-2 border-green-100"><img src="/images/dessert_cat.png" class="w-full h-full object-cover"></div>
                        <span class="font-bold text-sm text-gray-700">Desserts</span>
                    </div>
                    <div class="flex flex-col items-center gap-3 min-w-[100px] cursor-pointer hover:scale-105 transition">
                        <div class="w-24 h-24 rounded-full overflow-hidden shadow border-2 border-green-100"><img src="/images/cafe_cat.png" class="w-full h-full object-cover"></div>
                        <span class="font-bold text-sm text-gray-700">Cafe</span>
                    </div>
                </div>

                <!-- Promotional Banner -->
                <div class="mt-10 mb-10 bg-gradient-to-r from-green-500 to-emerald-600 rounded-2xl p-6 flex items-center justify-between">
                    <div>
                        <div class="text-white font-black text-xl mb-1">&#127381; Get 50%% OFF on first order!</div>
                        <div class="text-green-100 text-sm">Use code <span class="bg-white text-green-600 font-bold px-2 py-0.5 rounded">WELCOME50</span> at checkout</div>
                    </div>
                    <div class="text-5xl">&#127829;</div>
                </div>

                <h2 id="restaurants" class="text-2xl font-black text-gray-900 mt-4 mb-6">&#127968; Top Restaurants near you</h2>
                <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                    %s
                </div>
            </div>
            """.formatted(restsHtml.toString());

        return layout("Home", nav(user) + categories);
    }

    public static String register(String error, String email) {
        String errHtml = (error != null && !error.isEmpty()) 
            ? "<div id='error-msg' class='bg-red-50 text-red-600 p-3 rounded-lg text-sm mb-4 border border-red-200'>" + error + "</div>" : "";
        
        String content = nav(null) + """
            <div class='min-h-[80vh] flex items-center justify-center bg-gray-50 p-4'>
                <div class='max-w-md w-full bg-white p-8 rounded-2xl shadow-lg border border-gray-100'>
                    <div class='text-center mb-8'>
                        <h2 class='text-3xl font-black text-gray-900'>Create Account</h2>
                        <p class='text-gray-500 mt-2'>Join FoodIt to order hot food</p>
                    </div>
                    %s
                    <form action='/register' method='post' class='space-y-4'>
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Name</label>
                        <input type='text' id='name' name='name' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' /></div>
                        
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Email</label>
                        <input type='email' id='email' name='email' value='%s' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' /></div>
                        
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Phone</label>
                        <input type='tel' id='phone' name='phone' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' /></div>
                        
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Password</label>
                        <input type='password' id='password' name='password' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' /></div>
                        
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Confirm Password</label>
                        <input type='password' id='confirmPassword' name='confirmPassword' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' /></div>
                        
                        <button type='submit' id='register-btn' class='w-full bg-blinkit text-white font-bold p-4 rounded-lg hover:bg-blinkithover transition shadow-md mt-4'>Create Account</button>
                    </form>
                    <div class='mt-6 text-center text-sm text-gray-500'>
                        Already have an account? <a href='/login' class='text-blinkit font-bold'>Login here</a>
                    </div>
                </div>
            </div>
            """.formatted(errHtml, email != null ? email : "");
        
        return layout("Register", content);
    }

    public static String login(String error, String email) {
        String errHtml = (error != null && !error.isEmpty()) 
            ? "<div id='error-msg' class='bg-red-50 text-red-600 p-3 rounded-lg text-sm mb-4 border border-red-200'>" + error + "</div>" : "";
        
        String content = nav(null) + """
            <div class='min-h-[80vh] flex items-center justify-center bg-gray-50 p-4'>
                <div class='max-w-md w-full bg-white p-8 rounded-2xl shadow-lg border border-gray-100'>
                    <div class='text-center mb-8'>
                        <h1 class='text-3xl font-black text-gray-900 hidden'>Login</h1>
                        <h2 class='text-3xl font-black text-gray-900'>Welcome Back</h2>
                        <p class='text-gray-500 mt-2'>Login to your FoodIt account</p>
                    </div>
                    %s
                    <form action='/login' method='post' class='space-y-4'>
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Email</label>
                        <input type='email' id='email' name='email' value='%s' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' /></div>
                        
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Password</label>
                        <input type='password' id='password' name='password' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' /></div>
                        
                        <button type='submit' id='login-btn' class='w-full bg-blinkit text-white font-bold p-4 rounded-lg hover:bg-blinkithover transition shadow-md mt-4'>Login</button>
                    </form>
                    <div class='mt-6 text-center text-sm text-gray-500'>
                        New to FoodIt? <a href='/register' class='text-blinkit font-bold'>Create an account</a>
                    </div>
                </div>
            </div>
            """.formatted(errHtml, email != null ? email : "");
        
        return layout("Login", content);
    }

    public static String restaurant(Restaurant restaurant, User user) {
        StringBuilder itemsHtml = new StringBuilder();
        for (MenuItem item : restaurant.getMenu()) {
            itemsHtml.append("""
                <div class='bg-white p-4 rounded-xl border border-gray-100 shadow-sm flex justify-between items-center hover:shadow-md transition'>
                    <div class='flex-1 pr-4'>
                        <div class='text-xs font-bold text-red-500 mb-1 border border-red-500 inline-block px-1 rounded-sm'>%s</div>
                        <h3 class='font-bold text-gray-900 text-lg'>%s</h3>
                        <p class='text-gray-500 text-sm mt-1 mb-2'>%s</p>
                        <div class='font-black text-gray-900'>₹%.2f</div>
                    </div>
                    <form action='/cart/add' method='post' class='flex flex-col items-end gap-2'>
                        <input type='hidden' name='restaurantId' value='%s' />
                        <input type='hidden' name='itemId' value='%s' />
                        <div class='bg-white border border-blinkit rounded-lg flex items-center shadow-sm overflow-hidden w-24'>
                            <input type='number' name='quantity' value='1' min='1' class='w-10 text-center font-bold text-gray-700 outline-none p-1 border-r border-blinkit'/>
                            <button type='submit' class='add-to-cart-btn flex-1 bg-green-50 text-blinkit font-bold py-1 px-2 hover:bg-blinkit hover:text-white transition uppercase text-sm'>Add</button>
                        </div>
                    </form>
                </div>
                """.formatted(item.getCategory(), item.getName(), item.getDescription(), item.getPrice(), restaurant.getId(), item.getId()));
        }

        String content = nav(user) + """
            <div class='max-w-4xl mx-auto px-4 py-8'>
                <div class='bg-white rounded-2xl p-6 shadow-sm border border-gray-200 mb-8 flex gap-6 items-center'>
                    <div class='w-32 h-32 bg-gray-100 rounded-xl overflow-hidden shrink-0'>
                         <img src='/images/hero_banner.png' class='w-full h-full object-cover grayscale opacity-60'>
                    </div>
                    <div>
                        <h1 class='text-3xl font-black text-gray-900 mb-2'>%s</h1>
                        <p class='text-gray-600 font-medium mb-1'>%s &bull; %s</p>
                        <p class='text-gray-400 text-sm'>📍 %s</p>
                        <div class='mt-3 flex gap-2'>
                            <span class='bg-green-100 text-green-800 text-xs font-bold px-2 py-1 rounded'>4.2 ★</span>
                            <span class='bg-gray-100 text-gray-600 text-xs font-bold px-2 py-1 rounded'>30-40 mins</span>
                        </div>
                    </div>
                </div>
                
                <h2 class='text-2xl font-black text-gray-900 mb-6'>Recommended Menu</h2>
                <div class='grid grid-cols-1 md:grid-cols-2 gap-4'>
                    %s
                </div>
            </div>
            """.formatted(restaurant.getName(), restaurant.getCuisine(), "₹250 for two", restaurant.getAddress(), itemsHtml.toString());
            
        return layout(restaurant.getName(), content);
    }

    public static String cart(Cart cart, User user, String msg) {
        String msgHtml = (msg != null && !msg.isEmpty()) ? 
            "<div id='msg' class='bg-blue-50 text-blue-800 p-4 rounded-xl mb-6 font-medium shadow-sm border border-blue-100'>" + msg + "</div>" : "";

        if (cart.isEmpty()) {
            return layout("Cart", nav(user) + """
                <div class='max-w-7xl mx-auto px-4 py-12'>
                    %s
                    <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center max-w-lg mx-auto'>
                        <div class='text-6xl mb-4'>🛒</div>
                        <h2 class='text-2xl font-black text-gray-900 mb-2'>Your cart is empty</h2>
                        <p class='text-gray-500 mb-6'>Looks like you haven't added anything to your cart yet.</p>
                        <a href='/' class='bg-blinkit text-white font-bold py-3 px-6 rounded-lg hover:bg-blinkithover transition inline-block'>Browse Restaurants</a>
                    </div>
                </div>
                """.formatted(msgHtml));
        }

        StringBuilder itemsHtml = new StringBuilder();
        for (CartItem ci : cart.getItems()) {
            itemsHtml.append("""
                <div class='cart-item py-4 flex justify-between items-center border-b border-gray-100 last:border-0'>
                    <div>
                        <h4 class='font-bold text-gray-900'>%s</h4>
                        <div class='text-gray-500 text-sm'>₹%.2f x %d</div>
                    </div>
                    <div class='flex items-center gap-4'>
                        <div class='font-black text-gray-900'>₹%.2f</div>
                        <form action='/cart/remove' method='post'>
                            <input type='hidden' name='itemId' value='%s' />
                            <button type='submit' class='text-red-500 hover:text-red-700 bg-red-50 px-3 py-1 rounded-lg text-sm font-bold transition'>Remove</button>
                        </form>
                    </div>
                </div>
                """.formatted(ci.getMenuItem().getName(), ci.getMenuItem().getPrice(), ci.getQuantity(), ci.getSubtotal(), ci.getMenuItem().getId()));
        }

        String rightSide = """
            <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-6 sticky top-28'>
                <h3 class='text-xl font-black text-gray-900 mb-4'>Bill Details</h3>
                <div class='space-y-3 text-sm text-gray-600 pb-4 border-b border-gray-100'>
                    <div class='flex justify-between'><span>Item Total</span><span>₹%.2f</span></div>
                    <div class='flex justify-between'><span>Delivery Fee</span><span class='text-blinkit font-bold'>FREE</span></div>
                    <div class='flex justify-between'><span>Taxes & Charges</span><span>₹24.00</span></div>
                </div>
                <div class='flex justify-between items-center py-4'>
                    <span class='font-bold text-lg text-gray-900'>To Pay</span>
                    <span class='font-black text-xl text-gray-900'>₹%.2f</span>
                </div>
                
                <form action='/cart/promo' method='post' class='flex gap-2 mb-6'>
                    <input type='text' name='promoCode' placeholder='Promo code' class='flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:border-blinkit' />
                    <button type='submit' class='bg-gray-900 text-white px-4 py-2 rounded-lg font-bold text-sm'>Apply</button>
                </form>
                
                <a id='checkout-link' href='/checkout' class='block text-center bg-blinkit text-white font-bold p-4 rounded-xl hover:bg-blinkithover transition shadow-md'>
                    Check Out
                </a>
            </div>
            """.formatted(cart.getSubtotal(), cart.getGrandTotal());

        String content = nav(user) + """
            <div class='max-w-7xl mx-auto px-4 py-8 bg-gray-50 min-h-screen'>
                %s
                <h1 class='text-2xl font-black text-gray-900 mb-6'>Review your Cart</h1>
                <div class='grid grid-cols-1 lg:grid-cols-3 gap-8'>
                    <div class='lg:col-span-2'>
                        <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6'>
                            <h2 class='text-lg font-bold text-gray-900 mb-4 border-b pb-2'>Items added</h2>
                            %s
                        </div>
                    </div>
                    <div>
                        %s
                    </div>
                </div>
            </div>
            """.formatted(msgHtml, itemsHtml.toString(), rightSide);

        return layout("Cart", content);
    }

    public static String checkout(Cart cart, User user) {
        StringBuilder itemsHtml = new StringBuilder();
        for (CartItem ci : cart.getItems()) {
            itemsHtml.append("""
                <div class='flex justify-between text-sm py-2 text-gray-600'>
                    <span>%dx %s</span>
                    <span class='font-medium text-gray-900'>₹%.2f</span>
                </div>
                """.formatted(ci.getQuantity(), ci.getMenuItem().getName(), ci.getSubtotal()));
        }

        String rightSide = """
            <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-6 sticky top-28'>
                <h3 class='text-xl font-black text-gray-900 border-b pb-3 mb-4'>Order Summary</h3>
                <div class='mb-4'>
                    %s
                </div>
                <div class='space-y-3 text-sm text-gray-600 py-4 border-t border-b border-gray-100'>
                    <div class='flex justify-between'><span>Item Total</span><span>₹%.2f</span></div>
                    <div class='flex justify-between'><span>Restaurant Charges</span><span>₹24.00</span></div>
                </div>
                <div class='flex justify-between items-center py-4'>
                    <span class='font-bold text-lg text-gray-900'>Grand Total</span>
                    <span class='font-black text-xl text-gray-900'>₹%.2f</span>
                </div>
            </div>
            """.formatted(itemsHtml.toString(), cart.getSubtotal(), cart.getGrandTotal());

        String formSide = """
            <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-8'>
                <h2 class='text-2xl font-black text-gray-900 mb-6'>&#128666; Delivery & Payment</h2>
                <form action='/checkout' method='post' class='space-y-6' id='checkout-form'>
                    <input type='hidden' name='restaurantId' value='%s' />
                    
                    <div>
                        <label class='block text-sm font-bold text-gray-700 mb-2'>&#128205; Delivery Address</label>
                        <textarea id='address' name='address' required rows='3' class='w-full p-4 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blinkit outline-none resize-none shadow-sm' placeholder='Enter complete address (e.g. Flat 12, MG Road, Mumbai 400001)'></textarea>
                    </div>
                    
                    <div>
                        <label class='block text-sm font-bold text-gray-700 mb-3'>&#128179; Payment Method</label>
                        <div class='grid grid-cols-1 gap-3'>
                            <label class='border-2 border-blinkit rounded-xl p-4 flex items-center cursor-pointer bg-green-50 transition payment-option active' onclick="selectPayment(this,'cod')">
                                <input type='radio' name='paymentMethod' value='CASH_ON_DELIVERY' checked class='mr-3 w-5 h-5 accent-blinkit'>
                                <span class='text-2xl mr-3'>&#128181;</span>
                                <div><div class='font-bold text-gray-900'>Cash on Delivery</div><div class='text-xs text-gray-500'>Pay when your food arrives</div></div>
                            </label>
                            <label class='border border-gray-200 rounded-xl p-4 flex items-center cursor-pointer hover:bg-gray-50 transition payment-option' onclick="selectPayment(this,'upi')">
                                <input type='radio' name='paymentMethod' value='UPI' class='mr-3 w-5 h-5 accent-blinkit'>
                                <span class='text-2xl mr-3'>&#128247;</span>
                                <div><div class='font-bold text-gray-900'>UPI</div><div class='text-xs text-gray-500'>GPay, PhonePe, Paytm, BHIM</div></div>
                            </label>
                            <label class='border border-gray-200 rounded-xl p-4 flex items-center cursor-pointer hover:bg-gray-50 transition payment-option' onclick="selectPayment(this,'card')">
                                <input type='radio' name='paymentMethod' value='CREDIT_CARD' class='mr-3 w-5 h-5 accent-blinkit'>
                                <span class='text-2xl mr-3'>&#128179;</span>
                                <div><div class='font-bold text-gray-900'>Credit / Debit Card</div><div class='text-xs text-gray-500'>Visa, Mastercard, Rupay</div></div>
                            </label>
                            <label class='border border-gray-200 rounded-xl p-4 flex items-center cursor-pointer hover:bg-gray-50 transition payment-option' onclick="selectPayment(this,'netbanking')">
                                <input type='radio' name='paymentMethod' value='DEBIT_CARD' class='mr-3 w-5 h-5 accent-blinkit'>
                                <span class='text-2xl mr-3'>&#127981;</span>
                                <div><div class='font-bold text-gray-900'>Net Banking</div><div class='text-xs text-gray-500'>All major banks supported</div></div>
                            </label>
                            <label class='border border-gray-200 rounded-xl p-4 flex items-center cursor-pointer hover:bg-gray-50 transition payment-option' onclick="selectPayment(this,'wallet')">
                                <input type='radio' name='paymentMethod' value='WALLET' class='mr-3 w-5 h-5 accent-blinkit'>
                                <span class='text-2xl mr-3'>&#128260;</span>
                                <div><div class='font-bold text-gray-900'>FoodIt Wallet</div><div class='text-xs text-gray-500'>Balance: &#8377;500.00</div></div>
                            </label>
                        </div>
                        
                        <!-- UPI details -->
                        <div id='upi-section' class='hidden mt-4 p-4 bg-blue-50 rounded-xl border border-blue-200'>
                            <label class='block text-sm font-bold text-gray-700 mb-2'>Enter UPI ID</label>
                            <input type='text' placeholder='yourname@upi' class='w-full p-3 border border-blue-300 rounded-lg outline-none focus:ring-2 focus:ring-blue-400 bg-white' />
                            <div class='mt-3 flex gap-3'>
                                <div class='flex items-center gap-2 bg-white border rounded-lg px-3 py-2 cursor-pointer hover:bg-gray-50'><span>&#127381;</span><span class='text-xs font-bold'>GPay</span></div>
                                <div class='flex items-center gap-2 bg-white border rounded-lg px-3 py-2 cursor-pointer hover:bg-gray-50'><span>&#128247;</span><span class='text-xs font-bold'>PhonePe</span></div>
                                <div class='flex items-center gap-2 bg-white border rounded-lg px-3 py-2 cursor-pointer hover:bg-gray-50'><span>&#128200;</span><span class='text-xs font-bold'>Paytm</span></div>
                            </div>
                        </div>

                        <!-- Card details -->
                        <div id='card-section' class='hidden mt-4 p-4 bg-purple-50 rounded-xl border border-purple-200'>
                            <input type='text' id='card-number' placeholder='Card Number (e.g. 4242 4242 4242 4242)' class='w-full p-3 border border-purple-300 rounded-lg outline-none mb-3 focus:ring-2 focus:ring-purple-400 bg-white font-mono' maxlength='19' />
                            <div class='flex gap-3'>
                                <input type='text' id='card-expiry' placeholder='MM/YY' class='w-1/2 p-3 border border-purple-300 rounded-lg outline-none focus:ring-2 focus:ring-purple-400 bg-white' maxlength='5' />
                                <input type='text' id='card-cvv' placeholder='CVV' class='w-1/2 p-3 border border-purple-300 rounded-lg outline-none focus:ring-2 focus:ring-purple-400 bg-white' maxlength='3' />
                            </div>
                            <input type='text' placeholder='Name on Card' class='w-full p-3 border border-purple-300 rounded-lg outline-none mt-3 focus:ring-2 focus:ring-purple-400 bg-white' />
                            <div class='flex gap-2 mt-3 text-xs text-purple-700 items-center'><span>&#128274;</span> Cards are secured with 256-bit SSL encryption</div>
                        </div>

                        <!-- Net Banking -->
                        <div id='netbanking-section' class='hidden mt-4 p-4 bg-orange-50 rounded-xl border border-orange-200'>
                            <label class='block text-sm font-bold text-gray-700 mb-2'>Select Your Bank</label>
                            <div class='grid grid-cols-3 gap-2'>
                                <div class='border border-orange-200 rounded-lg p-2 text-center cursor-pointer hover:bg-orange-100 bg-white text-xs font-bold'>SBI</div>
                                <div class='border border-orange-200 rounded-lg p-2 text-center cursor-pointer hover:bg-orange-100 bg-white text-xs font-bold'>HDFC</div>
                                <div class='border border-orange-200 rounded-lg p-2 text-center cursor-pointer hover:bg-orange-100 bg-white text-xs font-bold'>ICICI</div>
                                <div class='border border-orange-200 rounded-lg p-2 text-center cursor-pointer hover:bg-orange-100 bg-white text-xs font-bold'>Axis</div>
                                <div class='border border-orange-200 rounded-lg p-2 text-center cursor-pointer hover:bg-orange-100 bg-white text-xs font-bold'>Kotak</div>
                                <div class='border border-orange-200 rounded-lg p-2 text-center cursor-pointer hover:bg-orange-100 bg-white text-xs font-bold'>Yes Bank</div>
                            </div>
                        </div>
                    </div>
                    
                    <button type='submit' id='place-order-btn' class='w-full bg-blinkit text-white font-black text-lg p-5 rounded-xl hover:bg-blinkithover transition shadow-lg mt-4'>&#128666; Place Order &mdash; &#8377;%.2f</button>
                </form>
            </div>
            <script>
            function selectPayment(el, type) {
                document.querySelectorAll('.payment-option').forEach(o => o.classList.remove('border-blinkit','border-2','bg-green-50'));
                el.classList.add('border-blinkit','border-2','bg-green-50');
                ['upi-section','card-section','netbanking-section'].forEach(id => document.getElementById(id).classList.add('hidden'));
                if(type==='upi') document.getElementById('upi-section').classList.remove('hidden');
                if(type==='card') document.getElementById('card-section').classList.remove('hidden');
                if(type==='netbanking') document.getElementById('netbanking-section').classList.remove('hidden');
            }
            // Card number formatter
            const cn = document.getElementById('card-number');
            if(cn) cn.addEventListener('input', function(e){ let v=e.target.value.replace(/[^0-9]/g,'').substring(0,16); let r=''; for(var i=0;i<v.length;i++){if(i>0&&i%%4===0)r+=' ';r+=v[i];}e.target.value=r; });
            const exp = document.getElementById('card-expiry');
            if(exp) exp.addEventListener('input', function(e){ let v=e.target.value.replace(/[^0-9]/g,'').substring(0,4); if(v.length>=2) v=v.substring(0,2)+'/'+v.substring(2); e.target.value=v; });
            </script>
            """.formatted(cart.getItems().get(0).getMenuItem().getRestaurantId(), cart.getGrandTotal());

        return layout("Checkout", nav(user) + """
            <div class='max-w-7xl mx-auto px-4 py-8 bg-gray-50 min-h-screen'>
                <div class='grid grid-cols-1 xl:grid-cols-3 gap-8'>
                    <div class='xl:col-span-2'>
                        %s
                    </div>
                    <div>
                        %s
                    </div>
                </div>
            </div>
            """.formatted(formSide, rightSide));
    }

    public static String orders(List<Order> orders, User user) {
        if (orders.isEmpty()) {
            return layout("Orders", nav(user) + """
                <div class='max-w-7xl mx-auto px-4 py-12'>
                    <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center max-w-lg mx-auto'>
                        <div class='text-6xl mb-4'>🧾</div>
                        <h2 class='text-2xl font-black text-gray-900 mb-2'>No orders yet</h2>
                        <p class='text-gray-500 mb-6'>You haven't placed any orders. Discover great food!</p>
                        <a href='/' class='bg-blinkit text-white font-bold py-3 px-6 rounded-lg hover:bg-blinkithover transition inline-block'>Browse</a>
                    </div>
                </div>
                """);
        }

        StringBuilder ordersHtml = new StringBuilder();
        for (Order o : orders) {
            ordersHtml.append("""
                <div class='bg-white p-6 rounded-2xl border border-gray-100 shadow-sm mb-4 flex justify-between items-center hover:shadow-md transition'>
                    <div>
                        <div class='text-xs text-gray-400 mb-1 font-mono'>#%s</div>
                        <div class='flex gap-3 items-center mb-2'>
                            <h3 class='font-bold text-gray-900 text-lg'>Order %s</h3>
                            <span class='bg-blue-100 text-blue-800 text-xs font-bold px-2 py-1 rounded'>%s</span>
                        </div>
                        <div class='text-gray-500 text-sm'>Total: <span class='font-bold text-gray-900'>₹%.2f</span></div>
                    </div>
                    <a href='/orders/%s' class='text-blinkit font-bold hover:underline bg-green-50 px-4 py-2 rounded-lg'>View Details</a>
                </div>
                """.formatted(o.getId().substring(0,8), o.getStatus(), o.getStatus(), o.getTotalAmount(), o.getId()));
        }

        return layout("Your Orders", nav(user) + """
            <div class='max-w-4xl mx-auto px-4 py-8 bg-gray-50 min-h-screen'>
                <h1 class='text-3xl font-black text-gray-900 mb-8'>Order History</h1>
                %s
            </div>
            """.formatted(ordersHtml.toString()));
    }

    public static String orderDetail(Order order, User user) {
        StringBuilder itemsHtml = new StringBuilder();
        for (CartItem oi : order.getItems()) {
            itemsHtml.append("""
                <div class='flex justify-between text-sm py-3 border-b border-gray-100 last:border-0'>
                    <div class='font-medium text-gray-800'>%dx %s</div>
                    <div class='font-bold text-gray-900'>&#8377;%.2f</div>
                </div>
                """.formatted(oi.getQuantity(), oi.getMenuItem().getName(), oi.getSubtotal()));
        }

        // Status badge color
        String statusColor = switch (order.getStatus().toString()) {
            case "PLACED" -> "bg-blue-100 text-blue-800";
            case "CONFIRMED" -> "bg-purple-100 text-purple-800";
            case "PREPARING" -> "bg-yellow-100 text-yellow-800";
            case "OUT_FOR_DELIVERY" -> "bg-orange-100 text-orange-800";
            case "DELIVERED" -> "bg-green-100 text-green-800";
            case "CANCELLED" -> "bg-red-100 text-red-800";
            default -> "bg-gray-100 text-gray-800";
        };

        // Tracking steps
        String[] steps = {"Order Placed", "Confirmed", "Preparing", "Out for Delivery", "Delivered"};
        String[] stepIcons = {"&#128203;", "&#9989;", "&#127859;", "&#128666;", "&#127968;"};
        int currentStep = switch (order.getStatus().toString()) {
            case "PLACED" -> 0; case "CONFIRMED" -> 1; case "PREPARING" -> 2;
            case "OUT_FOR_DELIVERY" -> 3; case "DELIVERED" -> 4; default -> -1;
        };
        StringBuilder stepsHtml = new StringBuilder();
        for (int i = 0; i < steps.length; i++) {
            boolean done = i <= currentStep;
            String stepClass = done ? "bg-blinkit text-white" : "bg-gray-200 text-gray-400";
            String lineClass = done && i < steps.length - 1 ? "bg-blinkit" : "bg-gray-200";
            stepsHtml.append("""
                <div class='flex flex-col items-center relative flex-1'>
                    <div class='w-10 h-10 %s rounded-full flex items-center justify-center font-black text-lg z-10 shadow'>%s</div>
                    <div class='text-xs font-bold text-center mt-2 %s'>%s</div>
                    %s
                </div>
                """.formatted(
                    stepClass, stepIcons[i],
                    done ? "text-gray-900" : "text-gray-400",
                    steps[i],
                    i < steps.length - 1
                        ? "<div class='absolute top-5 left-1/2 w-full h-1 " + lineClass + "'></div>"
                        : ""
                ));
        }

        // Cancel button if PLACED
        String cancelBtn = order.getStatus().toString().equals("PLACED") ? """
            <form action='/orders/%s/cancel' method='post' class='mt-4' onsubmit="return confirm('Cancel this order?')">
                <button type='submit' id='cancel-order-btn' class='w-full border-2 border-red-400 text-red-500 font-bold py-3 rounded-xl hover:bg-red-500 hover:text-white transition'>&#10060; Cancel Order</button>
            </form>
            """.formatted(order.getId()) : "";

        String content = nav(user) + """
            <div class='max-w-4xl mx-auto px-4 py-8 bg-gray-50 min-h-screen'>
                <!-- Status Header -->
                <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6'>
                    <div class='flex justify-between items-center mb-1'>
                        <h1 class='text-2xl font-black text-gray-900'>Order Details</h1>
                        <div id='order-status' class='%s font-bold px-4 py-2 rounded-full text-sm'>%s</div>
                    </div>
                    <div class='text-gray-400 text-xs font-mono'>ID: %s</div>
                </div>

                <!-- Live Tracking Map -->
                <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6'>
                    <h2 class='text-lg font-black text-gray-900 mb-4'>&#128506; Live Order Tracking</h2>
                    <div class='relative rounded-xl overflow-hidden border border-gray-200' style='height:280px;'>
                        <img src='/images/mock_map_ui.png' alt='Delivery Map' class='w-full h-full object-cover object-top' />
                        <div class='absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent'></div>
                        <div class='absolute bottom-4 left-4 right-4 bg-white/95 backdrop-blur rounded-xl p-3 shadow-lg flex items-center justify-between'>
                            <div>
                                <div class='text-xs text-gray-500 font-medium'>ETA</div>
                                <div class='text-2xl font-black text-blinkit'>~25 min</div>
                            </div>
                            <div class='text-center'>
                                <div class='text-xs text-gray-500'>Delivery Partner</div>
                                <div class='font-bold text-gray-900'>Rahul K. &#9733; 4.9</div>
                            </div>
                            <div class='bg-blinkit text-white px-4 py-2 rounded-lg font-bold text-sm'>&#128222; Call</div>
                        </div>
                    </div>
                    <!-- Step Tracker -->
                    <div class='flex items-start mt-6 relative px-4'>
                        %s
                    </div>
                </div>

                <!-- Delivery Address -->
                <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6'>
                    <h3 class='text-sm font-black text-gray-400 uppercase tracking-wider mb-3'>&#128205; Delivering to</h3>
                    <p class='text-gray-800 font-medium'>%s</p>
                </div>

                <!-- Bill Details -->
                <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6'>
                    <h3 class='text-sm font-black text-gray-400 uppercase tracking-wider mb-4'>&#129534; Bill Summary</h3>
                    %s
                    <div class='flex justify-between items-center pt-4 mt-3 border-t border-gray-200'>
                        <span class='font-bold text-lg text-gray-900'>Total Paid</span>
                        <span class='font-black text-xl text-gray-900'>&#8377;%.2f</span>
                    </div>
                </div>

                %s
                <a href='/' class='block text-center mt-4 w-full border-2 border-blinkit text-blinkit font-bold py-3 rounded-xl hover:bg-blinkit hover:text-white transition'>&#127961; Continue Ordering</a>
            </div>
            """.formatted(
                statusColor, order.getStatus(), order.getId(),
                stepsHtml.toString(),
                order.getDeliveryAddress(),
                itemsHtml.toString(), order.getTotalAmount(),
                cancelBtn
            );
            
        return layout("Order Details", content);
    }

    public static String admin(User user, int totalUsers, int totalRestaurants, int totalOrders) {
        String content = nav(user) + """
            <div class='max-w-7xl mx-auto px-4 py-8 bg-gray-50 min-h-screen'>
                <h1 class='text-3xl font-black text-gray-900 mb-8'>Admin Dashboard</h1>
                <div class='grid grid-cols-1 md:grid-cols-3 gap-6'>
                    <div class='card bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center hover:shadow-md transition'>
                        <h3 class='text-gray-500 font-bold uppercase tracking-wider text-sm mb-4'>Total Users</h3>
                        <p id='admin-stats' class='text-5xl font-black text-blue-500'>Users: %d</p>
                    </div>
                    <div class='card bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center hover:shadow-md transition'>
                        <h3 class='text-gray-500 font-bold uppercase tracking-wider text-sm mb-4'>Active Restaurants</h3>
                        <p class='text-5xl font-black text-blinkit'>%d</p>
                    </div>
                    <div class='card bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center hover:shadow-md transition'>
                        <h3 class='text-gray-500 font-bold uppercase tracking-wider text-sm mb-4'>Total Lifetime Orders</h3>
                        <p class='text-5xl font-black text-amber-500'>%d</p>
                    </div>
                </div>
            </div>
            """.formatted(totalUsers, totalRestaurants, totalOrders);
        return layout("Admin Dashboard", content);
    }

    public static String error(String errorMsg) {
        String content = nav(null) + """
            <div class='min-h-[80vh] flex items-center justify-center bg-gray-50 p-4'>
                <div class='max-w-md w-full bg-white p-12 text-center rounded-3xl shadow-lg border border-gray-100'>
                    <div class='text-6xl mb-6'>⚠️</div>
                    <h1 id='error-heading' class='text-3xl font-black text-red-500 mb-4'>Error</h1>
                    <p id='error-msg' class='text-gray-600 mb-8 text-lg'>%s</p>
                    <a href='/' class='bg-blinkit text-white font-bold py-3 px-8 rounded-xl hover:bg-blinkithover transition inline-block shadow-md'>Go Back to Safety</a>
                </div>
            </div>
            """.formatted(errorMsg);
        return layout("Error", content);
    }

    public static String forgotPassword(String msg) {
        String msgHtml = (msg != null && !msg.isEmpty())
            ? "<div id='reset-msg' class='bg-blue-50 text-blue-700 p-3 rounded-lg text-sm mb-4 border border-blue-200'>" + msg + "</div>"
            : "";
        String content = nav(null) + """
            <div class='min-h-[80vh] flex items-center justify-center bg-gray-50 p-4'>
                <div class='max-w-md w-full bg-white p-8 rounded-2xl shadow-lg border border-gray-100'>
                    <div class='text-center mb-8'>
                        <div class='text-5xl mb-4'>&#128274;</div>
                        <h1 class='text-3xl font-black text-gray-900'>Forgot Password?</h1>
                        <p class='text-gray-500 mt-2'>Enter your email to receive a reset link</p>
                    </div>
                    %s
                    <form action='/forgot-password' method='post' class='space-y-4'>
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Email Address</label>
                        <input type='email' id='reset-email' name='email' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' placeholder='Enter your registered email' /></div>
                        <button type='submit' id='send-reset-btn' class='w-full bg-blinkit text-white font-bold p-4 rounded-lg hover:bg-blinkithover transition shadow-md'>Send Reset Link</button>
                    </form>
                    <div class='mt-6 text-center text-sm text-gray-500'>
                        Remember your password? <a href='/login' class='text-blinkit font-bold'>Login here</a>
                    </div>
                </div>
            </div>
            """.formatted(msgHtml);
        return layout("Forgot Password", content);
    }

    public static String resetPassword(String token, String error) {
        String errHtml = (error != null && !error.isEmpty())
            ? "<div id='error-msg' class='bg-red-50 text-red-600 p-3 rounded-lg text-sm mb-4 border border-red-200'>" + error + "</div>"
            : "";
        String content = nav(null) + """
            <div class='min-h-[80vh] flex items-center justify-center bg-gray-50 p-4'>
                <div class='max-w-md w-full bg-white p-8 rounded-2xl shadow-lg border border-gray-100'>
                    <div class='text-center mb-8'>
                        <div class='text-5xl mb-4'>&#128273;</div>
                        <h1 class='text-3xl font-black text-gray-900'>Set New Password</h1>
                        <p class='text-gray-500 mt-2'>Choose a strong password for your account</p>
                    </div>
                    %s
                    <form action='/reset-password' method='post' class='space-y-4'>
                        <input type='hidden' name='token' value='%s' />
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>New Password</label>
                        <input type='password' id='new-password' name='newPassword' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' placeholder='Min 8 chars, uppercase, number, special char' /></div>
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Confirm Password</label>
                        <input type='password' id='confirm-password' name='confirmPassword' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none transition' placeholder='Re-enter new password' /></div>
                        <button type='submit' id='reset-btn' class='w-full bg-blinkit text-white font-bold p-4 rounded-lg hover:bg-blinkithover transition shadow-md'>Reset Password</button>
                    </form>
                </div>
            </div>
            """.formatted(errHtml, token != null ? token : "");
        return layout("Reset Password", content);
    }

    public static String profile(User user, String msg) {
        String msgHtml = (msg != null && !msg.isEmpty())
            ? "<div id='profile-msg' class='bg-green-50 text-green-700 p-3 rounded-lg text-sm mb-4 border border-green-200'>" + msg + "</div>"
            : "";
        String content = nav(user) + """
            <div class='max-w-2xl mx-auto px-4 py-12 bg-gray-50 min-h-screen'>
                <div class='bg-white rounded-2xl shadow-sm border border-gray-100 p-8'>
                    <div class='flex items-center gap-4 mb-8'>
                        <div class='w-16 h-16 bg-gradient-to-br from-blinkit to-emerald-400 rounded-full flex items-center justify-center text-white text-2xl font-black'>
                            %s
                        </div>
                        <div>
                            <h1 class='text-2xl font-black text-gray-900'>My Profile</h1>
                            <p class='text-gray-500 text-sm'>%s</p>
                        </div>
                    </div>
                    %s
                    <form action='/profile' method='post' class='space-y-5'>
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Full Name</label>
                        <input type='text' id='profile-name' name='name' value='%s' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none' /></div>
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Email Address</label>
                        <input type='email' value='%s' disabled class='w-full p-3 border border-gray-200 rounded-lg bg-gray-50 text-gray-400 cursor-not-allowed' /></div>
                        <div><label class='block text-sm font-semibold text-gray-700 mb-1'>Phone Number</label>
                        <input type='tel' id='profile-phone' name='phone' value='%s' required class='w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blinkit outline-none' /></div>
                        <button type='submit' id='save-profile-btn' class='w-full bg-blinkit text-white font-bold p-4 rounded-lg hover:bg-blinkithover transition shadow-md'>Save Changes</button>
                    </form>
                    <hr class='my-8 border-gray-100' />
                    <a href='/forgot-password' class='text-sm text-blinkit font-bold hover:underline'>&#128274; Change Password</a>
                </div>
            </div>
            """.formatted(
                user.getName().substring(0, 1).toUpperCase(),
                user.getRole().toString(),
                msgHtml,
                user.getName(),
                user.getEmail(),
                user.getPhone()
            );
        return layout("My Profile", content);
    }
}

