package com.example.trifhop.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trifhop.data.model.Product
import com.example.trifhop.data.repository.ProductRepository
import com.example.trifhop.data.local.WishlistManager
import com.example.trifhop.ui.navigation.Screen
import com.example.trifhop.ui.components.PremiumProductCard
import com.example.trifhop.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    navController: NavController,
    productRepository: ProductRepository,
    wishlistManager: WishlistManager
) {
    var wishlistProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    
    val scope = rememberCoroutineScope()

    // Load wishlist products
    LaunchedEffect(refreshTrigger) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            try {
                val wishlistIds = wishlistManager.getWishlistProductIds()
                
                if (wishlistIds.isEmpty()) {
                    wishlistProducts = emptyList()
                    isLoading = false
                    return@launch
                }
                
                // Load all products and filter by wishlist IDs
                val productsResult = productRepository.getProducts()
                if (productsResult.isSuccess) {
                    val allProducts = productsResult.getOrNull() ?: emptyList()
                    wishlistProducts = allProducts.filter { it.id in wishlistIds }
                } else {
                    errorMessage = productsResult.exceptionOrNull()?.message ?: "Gagal memuat produk"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Terjadi kesalahan"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Wishlist Saya",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    // Shimmer Loading Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(6) {
                            ShimmerProductCardItem()
                        }
                    }
                }
                
                errorMessage != null -> {
                    // Error State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { refreshTrigger++ },
                            colors = ButtonDefaults.buttonColors(containerColor = TrifhopBlue)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                
                wishlistProducts.isEmpty() -> {
                    // Animated Empty State
                    val pulseAnim = rememberInfiniteTransition(label = "pulse")
                    val heartScale by pulseAnim.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.15f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "heart_pulse"
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .scale(heartScale),
                            tint = TrifhopBlue.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Wishlist Masih Kosong",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Simpan produk favoritmu di sini.\nKetuk ❤ pada produk untuk menyimpannya!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { navController.navigate(Screen.Explore.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = TrifhopBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Jelajahi Produk", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                
                else -> {
                    // Content: Product Grid using PremiumProductCard
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(wishlistProducts) { index, product ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300, delayMillis = (index % 6) * 50)) +
                                        scaleIn(tween(300, delayMillis = (index % 6) * 50), initialScale = 0.92f)
                            ) {
                                PremiumProductCard(
                                    product = product,
                                    isInWishlist = true,
                                    onWishlistToggle = {
                                        wishlistManager.removeFromWishlist(product.id)
                                        refreshTrigger++
                                    },
                                    onClick = {
                                        navController.navigate(Screen.ProductDetail.createRoute(product.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Legacy stub — kept for backward compile compatibility
@Composable
fun WishlistProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onRemoveClick: () -> Unit
) {}
