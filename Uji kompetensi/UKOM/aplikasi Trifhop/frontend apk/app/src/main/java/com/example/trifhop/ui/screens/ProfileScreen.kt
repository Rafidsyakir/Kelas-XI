package com.example.trifhop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trifhop.data.local.UserPreferences
import com.example.trifhop.data.model.Order
import com.example.trifhop.data.repository.OrderRepository
import com.example.trifhop.ui.navigation.Screen
import com.example.trifhop.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userPreferences: UserPreferences,
    orderRepository: OrderRepository
) {
    var currentOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var pastOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val user = userPreferences.getUser()
    
    // For demo: use dummy user if not logged in
    val displayName = user?.name ?: "Demo User"
    val displayEmail = user?.email ?: "demo@trifhop.com"
    
    // Helper function to format price
    fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
        return "Rp ${formatter.format(amount.toLong())}"
    }
    
    // Load orders
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val token = userPreferences.getToken()
                if (!token.isNullOrBlank()) {
                    val result = orderRepository.getUserOrders(token)
                    result.onSuccess { orders ->
                        currentOrders = orders.filter { it.status.lowercase() in listOf("pending", "packed", "sent") }
                        pastOrders = orders.filter { it.status.lowercase() in listOf("finished", "cancelled") }
                    }
                }
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Card
            item {
                UserProfileCard(
                    userName = displayName,
                    userEmail = displayEmail,
                    onEditProfile = { /* Navigate to edit profile */ }
                )
            }
            
            // Quick Actions
            item {
                QuickActionsCard(
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                    onOrderHistoryClick = { navController.navigate(Screen.OrderHistory.route) },
                    onHelpClick = { /* Navigate to help */ },
                    onLogoutClick = { showLogoutDialog = true }
                )
            }
            
            // Orders Section Header
            item {
                Text(
                    text = "My Orders",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Tab Row
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        if (tabPositions.isNotEmpty()) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.fillMaxWidth(),
                                color = TrifhopBlue
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Current (${currentOrders.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Past (${pastOrders.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
            
            // Loading State
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TrifhopBlue)
                    }
                }
            } else {
                // Orders List
                val ordersToShow = if (selectedTab == 0) currentOrders else pastOrders
                
                if (ordersToShow.isEmpty()) {
                    item {
                        EmptyOrdersState(
                            message = if (selectedTab == 0) "No current orders" else "No past orders",
                            navController = navController
                        )
                    }
                } else {
                    items(ordersToShow, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            formatRupiah = ::formatRupiah,
                            onTrackOrder = {
                                // Navigate to order tracking screen
                            },
                            showTrackButton = selectedTab == 0
                        )
                    }
                }
            }
        }
    }
    
    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Logout")
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        userPreferences.clearUser()
                        showLogoutDialog = false
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserProfileCard(
    userName: String,
    userEmail: String,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = TrifhopBlue
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = TrifhopBlue
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            
            IconButton(onClick = onEditProfile) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun QuickActionsCard(
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            QuickActionItem(
                icon = Icons.Default.Receipt,
                title = "Order History",
                onClick = onOrderHistoryClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            QuickActionItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                onClick = onNotificationsClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            QuickActionItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = onSettingsClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            QuickActionItem(
                icon = Icons.Default.Help,
                title = "Help & Support",
                onClick = onHelpClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            QuickActionItem(
                icon = Icons.Default.Logout,
                title = "Logout",
                iconTint = ErrorRed,
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    title: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OrderCard(
    order: Order,
    formatRupiah: (Double) -> String,
    onTrackOrder: () -> Unit,
    showTrackButton: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                OrderStatusBadge(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Order Date
            Text(
                text = formatDate(order.createdAt),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Order Items Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order details",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "View Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrifhopBlue
                )
            }
            
            if (showTrackButton) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onTrackOrder,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrifhopBlue
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Track Order")
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (color, text) = when (status.lowercase()) {
        "pending" -> WarningOrange to "Pending"
        "processing" -> TrifhopBlue to "Processing"
        "shipped" -> TealAccent to "Shipped"
        "delivered" -> SuccessGreen to "Delivered"
        "cancelled" -> ErrorRed to "Cancelled"
        else -> MaterialTheme.colorScheme.onSurfaceVariant to status
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun EmptyOrdersState(
    message: String,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.ShoppingBag,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("home") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrifhopBlue
                )
            ) {
                Text("Start Shopping")
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}
