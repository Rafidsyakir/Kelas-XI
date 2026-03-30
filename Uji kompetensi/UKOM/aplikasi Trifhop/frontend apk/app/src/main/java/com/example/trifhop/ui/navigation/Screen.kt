package com.example.trifhop.ui.navigation

/**
 * Sealed class untuk Routes di aplikasi
 */
sealed class Screen(val route: String) {
    // Authentication
    data object Login : Screen("login")
    data object Register : Screen("register")
    
    // Main Navigation (Bottom Bar)
    data object Home : Screen("home")
    data object Explore : Screen("explore")
    data object Wishlist : Screen("wishlist")
    data object Cart : Screen("cart")
    data object Profile : Screen("profile")
    
    // Product
    data object ProductDetail : Screen("detail/{productId}") {
        fun createRoute(productId: Int) = "detail/$productId"
    }
    
    // Checkout & Orders
    data object Checkout : Screen("checkout")
    data object PaymentSuccess : Screen("payment_success")
    data object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: Int) = "order_tracking/$orderId"
    }

    // Profile Sub-screens
    data object Notifications : Screen("notifications")
    data object Settings : Screen("settings")
    data object OrderHistory : Screen("order_history")
}
