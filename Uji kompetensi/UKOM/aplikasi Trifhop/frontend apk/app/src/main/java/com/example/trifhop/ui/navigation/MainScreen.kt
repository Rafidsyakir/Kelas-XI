package com.example.trifhop.ui.navigation

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trifhop.data.local.CartManager
import com.example.trifhop.data.local.UserPreferences
import com.example.trifhop.data.local.WishlistManager
import com.example.trifhop.data.network.RetrofitClient
import com.example.trifhop.data.repository.OrderRepository
import com.example.trifhop.data.repository.ProductRepository
import com.example.trifhop.ui.screens.*
import com.example.trifhop.ui.components.*
import com.example.trifhop.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Main Screen dengan Bottom Navigation + Premium Animations
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Initialize dependencies
    val cartManager = CartManager.getInstance(context)
    val wishlistManager = WishlistManager.getInstance(context)
    val userPreferences = UserPreferences(context)
    val productRepository = ProductRepository(RetrofitClient.apiService)
    val orderRepository = OrderRepository(RetrofitClient.apiService)
    
    // Check if user is logged in
    val startDestination = if (userPreferences.isLoggedIn()) "home" else "login"
    
    val currentRoute by navController.currentBackStackEntryAsState()
    val currentRouteStr = currentRoute?.destination?.route ?: ""
    val showBottomBar = currentRouteStr in listOf("home", "explore", "wishlist", "cart", "profile") ||
        currentRouteStr.startsWith("explore?categoryId")
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { 
            if (showBottomBar) {
                BottomNavigationBar(navController) 
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(if (showBottomBar) paddingValues else PaddingValues(0.dp)),
            // Add smooth transitions
            enterTransition = { slideInFromRight() + fadeInTransition() },
            exitTransition = { fadeOutTransition() },
            popEnterTransition = { slideInFromLeft() + fadeInTransition() },
            popExitTransition = { slideOutToRight() + fadeOutTransition() }
        ) {
            // Authentication Screens
            composable(Screen.Login.route) {
                LoginScreen(
                    navController = navController,
                    userPreferences = userPreferences
                )
            }
            
            composable(Screen.Register.route) {
                RegisterScreen(
                    navController = navController,
                    userPreferences = userPreferences
                )
            }
            
            // Main Screens
            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    productRepository = productRepository,
                    cartManager = cartManager
                )
            }
            
            composable(Screen.Explore.route) {
                ExploreScreen(
                    navController = navController,
                    productRepository = productRepository,
                    cartManager = cartManager
                )
            }
            
            // Explore dengan filter category dari home screen
            composable(
                route = "explore?categoryId={categoryId}",
                arguments = listOf(navArgument("categoryId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getInt("categoryId")?.takeIf { it != -1 }
                ExploreScreen(
                    navController = navController,
                    productRepository = productRepository,
                    cartManager = cartManager,
                    categoryId = categoryId
                )
            }
            
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    navController = navController,
                    productRepository = productRepository,
                    wishlistManager = wishlistManager
                )
            }
            
            composable(Screen.Cart.route) {
                CartScreen(
                    navController = navController,
                    cartManager = cartManager,
                    userPreferences = userPreferences
                )
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    userPreferences = userPreferences,
                    orderRepository = orderRepository
                )
            }
            
            // Product Detail Screen
            composable(
                route = "detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                val viewModel: MainViewModel = viewModel()
                ProductDetailScreen(
                    productId = productId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            // Checkout & Payment Screens
            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    navController = navController,
                    cartManager = cartManager,
                    orderRepository = orderRepository,
                    userPreferences = userPreferences
                )
            }
            
            composable(Screen.PaymentSuccess.route) {
                PaymentSuccessScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onViewOrderStatus = {
                        navController.navigate(Screen.OrderHistory.route)
                    }
                )
            }

            // Order Tracking Screen
            composable(
                route = "order_tracking/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
                OrderTrackingScreen(
                    navController = navController,
                    orderRepository = orderRepository,
                    userPreferences = userPreferences,
                    orderId = orderId
                )
            }
            
            // Profile Sub-screens
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    navController = navController
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController = navController,
                    userPreferences = userPreferences
                )
            }
            
            composable(Screen.OrderHistory.route) {
                OrderHistoryScreen(
                    navController = navController,
                    orderRepository = orderRepository,
                    userPreferences = userPreferences
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { 
                Icon(
                    imageVector = if (currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true) 
                        Icons.Filled.Home 
                    else 
                        Icons.Outlined.Home,
                    contentDescription = "Home"
                ) 
            },
            label = { Text("Home") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        
        NavigationBarItem(
            icon = { 
                Icon(
                    imageVector = if (currentDestination?.hierarchy?.any { 
                        it.route == Screen.Explore.route || it.route?.startsWith("explore?") == true 
                    } == true) 
                        Icons.Filled.Search 
                    else 
                        Icons.Outlined.Search,
                    contentDescription = "Explore"
                ) 
            },
            label = { Text("Explore") },
            selected = currentDestination?.hierarchy?.any { 
                it.route == Screen.Explore.route || it.route?.startsWith("explore?") == true 
            } == true,
            onClick = {
                navController.navigate(Screen.Explore.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        
        NavigationBarItem(
            icon = { 
                Icon(
                    imageVector = if (currentDestination?.hierarchy?.any { it.route == Screen.Wishlist.route } == true)
                        Icons.Filled.Favorite
                    else
                        Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist"
                ) 
            },
            label = { Text("Wishlist") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Wishlist.route } == true,
            onClick = {
                navController.navigate(Screen.Wishlist.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        
        NavigationBarItem(
            icon = { 
                Icon(
                    imageVector = if (currentDestination?.hierarchy?.any { it.route == Screen.Cart.route } == true)
                        Icons.Filled.ShoppingCart
                    else
                        Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart"
                ) 
            },
            label = { Text("Cart") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Cart.route } == true,
            onClick = {
                navController.navigate(Screen.Cart.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        
        NavigationBarItem(
            icon = { 
                Icon(
                    imageVector = if (currentDestination?.hierarchy?.any { it.route == Screen.Profile.route } == true)
                        Icons.Filled.Person
                    else
                        Icons.Outlined.Person,
                    contentDescription = "Profile"
                ) 
            },
            label = { Text("Profile") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Profile.route } == true,
            onClick = {
                navController.navigate(Screen.Profile.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
