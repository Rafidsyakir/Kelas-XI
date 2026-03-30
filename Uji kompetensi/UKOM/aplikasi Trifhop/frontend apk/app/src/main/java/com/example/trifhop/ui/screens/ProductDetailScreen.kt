package com.example.trifhop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.trifhop.data.local.CartManager
import com.example.trifhop.data.local.WishlistManager
import com.example.trifhop.data.network.ApiConfig
import com.example.trifhop.ui.theme.*
import com.example.trifhop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val cartManager = remember { CartManager.getInstance(context) }
    val wishlistManager = remember { WishlistManager.getInstance(context) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Size selection state
    val availableSizes = listOf("S", "M", "L", "XL", "XXL")
    var selectedSize by remember { mutableStateOf("M") }
    
    val products by viewModel.products.collectAsState()
    val product = remember(products, productId) {
        products.firstOrNull { it.id == productId }
    }
    
    var isInWishlist by remember(product) {
        mutableStateOf(product?.let { wishlistManager.isInWishlist(it.id) } ?: false)
    }
    
    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TrifhopBlue)
        }
        return
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Product Detail",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share action */ }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // ═══════════════════════════════════════════════════════════════════════════
            //   PREMIUM STICKY ACTION BAR - Glassmorphism Effect
            // ═══════════════════════════════════════════════════════════════════════════
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 12.dp,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Wishlist Button - Premium Design
                    OutlinedButton(
                        onClick = {
                            isInWishlist = wishlistManager.toggleWishlist(product.id)
                            snackbarMessage = if (isInWishlist) "Added to wishlist ❤️" else "Removed from wishlist"
                            showSnackbar = true
                        },
                        modifier = Modifier
                            .weight(0.25f)
                            .height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 2.dp,
                            color = if (isInWishlist) TrifhopBlue else TrifhopBlue.copy(alpha = 0.3f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isInWishlist) TrifhopBlue.copy(alpha = 0.15f) else Color.Transparent
                        )
                    ) {
                        Icon(
                            imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isInWishlist) TrifhopBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    
                    // Add to Cart Button - Premium Design
                    Button(
                        onClick = {
                            product.let {
                                cartManager.addToCart(it)
                                snackbarMessage = "Added to cart! 🛒"
                                showSnackbar = true
                            }
                        },
                        modifier = Modifier
                            .weight(0.75f)
                            .height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrifhopBlue,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        ),
                        enabled = product.isAvailable(),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (product.isAvailable()) "Add to Cart" else "Sold Out",
                            modifier = Modifier.padding(vertical = 2.dp),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Show snackbar when item added
        LaunchedEffect(showSnackbar) {
            if (showSnackbar) {
                snackbarHostState.showSnackbar(
                    message = snackbarMessage,
                    duration = SnackbarDuration.Short
                )
                showSnackbar = false
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ═══════════════════════════════════════════════════════════════════════════
            //   PREMIUM PRODUCT IMAGE - Full Width with Premium Shadow
            // ═══════════════════════════════════════════════════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = product.getFullImageUrl(ApiConfig.getBaseUrl())
                    ),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Gradient Overlay for Better Badge Visibility
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                
                // Stock Badge (if low stock) - Premium Design
                if (product.stock <= 5 && product.stock > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(18.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = WarningOrange,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Filled.Inventory2,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Only ${product.stock} left",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            // ═══════════════════════════════════════════════════════════════════════════
            //   PRODUCT DETAILS CONTENT - Clean & Premium Layout
            // ═══════════════════════════════════════════════════════════════════════════
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Category Label - Premium Style
                product.category?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Label,
                            contentDescription = null,
                            tint = TrifhopBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = it.name.uppercase(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrifhopBlue,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Product Name - Larger & Bolder
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 34.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Price - Eye-Catching
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = product.getFormattedPrice(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = TrifhopBlue,
                        fontSize = 32.sp
                    )
                    // Price Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Great Deal",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Condition Badge Row - Enhanced Design
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TrifhopBlue.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, TrifhopBlue.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = TrifhopBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = product.condition,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TrifhopBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SuccessGreen.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, SuccessGreen.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Eco,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Eco Friendly",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                // Size Selection
                if (product.size.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Size",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        TextButton(onClick = { /* Size guide */ }) {
                            Text(
                                text = "Size Guide",
                                color = TrifhopBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TrifhopBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(availableSizes) { size ->
                            SizeChip(
                                size = size,
                                isSelected = selectedSize == size,
                                onClick = { selectedSize = size }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(24.dp))
                
                // Description
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(24.dp))
                
                // Seller Information Card
                SellerInfoCard()
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// Size Chip Component
@Composable
fun SizeChip(
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(56.dp)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) TrifhopBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) TrifhopBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = size,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) TrifhopBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Seller Info Card
@Composable
fun SellerInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Seller Avatar
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = TrifhopBlue.copy(alpha = 0.2f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = TrifhopBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Trifhop Official",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107), // gold star color
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.8 (120 reviews)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            OutlinedButton(
                onClick = { /* Visit store */ },
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TrifhopBlue),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = "Visit Store",
                    color = TrifhopBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
