package com.example.trifhop.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import com.example.trifhop.data.local.CartManager
import com.example.trifhop.data.local.UserPreferences
import com.example.trifhop.data.network.ApiConfig
import com.example.trifhop.ui.navigation.Screen
import com.example.trifhop.ui.components.*
import com.example.trifhop.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartManager: CartManager,
    userPreferences: UserPreferences
) {    var showLoginDialog by remember { mutableStateOf(false) }
    var cartItems by remember { mutableStateOf(cartManager.getCartItems()) }
    
    // Helper function to format price
    fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return "Rp ${formatter.format(amount.toLong())}"
    }
    
    val totalPrice = cartItems.sumOf { it.getTotalPrice() }
    val itemCount = cartItems.sumOf { it.quantity }

    // Dialog: perlu login sebelum checkout
    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            containerColor = Color(0xFF1A2332),
            icon = {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF137FEC), modifier = Modifier.size(40.dp))
            },
            title = {
                Text("Login Diperlukan", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Anda perlu login terlebih dahulu untuk melanjutkan checkout.", color = Color(0xFF94A3B8))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLoginDialog = false
                        navController.navigate("login")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137FEC))
                ) {
                    Text("Login Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Batal", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Shopping Cart",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = {
                            cartManager.clearCart()
                            cartItems = cartManager.getCartItems()
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Clear cart",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartBottomBar(
                    itemCount = itemCount,
                    totalPrice = totalPrice,
                    formatRupiah = ::formatRupiah,
                    onCheckout = {
                        if (userPreferences.isLoggedIn()) {
                            navController.navigate(Screen.Checkout.route)
                        } else {
                            showLoginDialog = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyCartState(
                onBrowseClick = { navController.navigate("home") }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(cartItems, key = { _, item -> item.product.id }) { index, cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        formatRupiah = ::formatRupiah,
                        onQuantityChange = { newQuantity ->
                            cartManager.updateQuantity(cartItem.product.id, newQuantity)
                            cartItems = cartManager.getCartItems()
                        },
                        onRemove = {
                            cartManager.removeFromCart(cartItem.product.id)
                            cartItems = cartManager.getCartItems()
                        },
                        modifier = Modifier.animateListItem(index)
                    )
                }
                
                // Order Summary Card
                item {
                    OrderSummaryCard(
                        subtotal = totalPrice,
                        shipping = 0.0,
                        total = totalPrice,
                        formatRupiah = ::formatRupiah
                    )
                }
                
                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

// Remove old EmptyCart component - using EmptyCartState from EmptyState.kt

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM CART ITEM CARD - Enhanced Material Design
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun CartItemCard(
    cartItem: CartManager.CartItem,
    formatRupiah: (Double) -> String,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Product Image
            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent
            ) {
                Image(
                    painter = rememberAsyncImagePainter(cartItem.product.getFullImageUrl(ApiConfig.getBaseUrl())),
                    contentDescription = cartItem.product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = cartItem.product.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        lineHeight = 22.sp
                    )
                    Surface(
                        onClick = onRemove,
                        modifier = Modifier.size(30.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Close, "Remove",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatRupiah(cartItem.product.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TrifhopBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = { onQuantityChange(cartItem.quantity - 1) },
                            modifier = Modifier.size(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Remove, "Decrease",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(
                            modifier = Modifier.size(width = 40.dp, height = 34.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(cartItem.quantity.toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Surface(
                            onClick = { onQuantityChange(cartItem.quantity + 1) },
                            enabled = cartItem.quantity < cartItem.product.stock,
                            modifier = Modifier.size(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (cartItem.quantity < cartItem.product.stock)
                                TrifhopBlue else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, "Increase",
                                    modifier = Modifier.size(16.dp),
                                    tint = if (cartItem.quantity < cartItem.product.stock)
                                        Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Text(
                        formatRupiah(cartItem.getTotalPrice()),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (cartItem.quantity >= cartItem.product.stock) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Warning, null, tint = WarningOrange,
                            modifier = Modifier.size(13.dp))
                        Text("Stok maksimal",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarningOrange)
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM ORDER SUMMARY CARD - Glassmorphism Style
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun OrderSummaryCard(
    subtotal: Double,
    shipping: Double,
    total: Double,
    formatRupiah: (Double) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Receipt, null, tint = TrifhopBlue,
                    modifier = Modifier.size(20.dp))
                Text("Ringkasan Pesanan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SummaryRow("Subtotal", formatRupiah(subtotal))
            Spacer(modifier = Modifier.height(10.dp))
            SummaryRow("Pengiriman", if (shipping == 0.0) "Dihitung saat checkout" else formatRupiah(shipping))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = TrifhopBlue.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(formatRupiah(total),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = TrifhopBlue)
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM BOTTOM BAR - Glassmorphism Checkout
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun CartBottomBar(
    itemCount: Int,
    totalPrice: Double,
    formatRupiah: (Double) -> String,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "$itemCount item",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    formatRupiah(totalPrice),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TrifhopBlue
                )
            }
            Button(
                onClick = onCheckout,
                colors = ButtonDefaults.buttonColors(containerColor = TrifhopBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, null,
                    modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Checkout",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
