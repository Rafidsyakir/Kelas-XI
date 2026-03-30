package com.example.trifhop.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trifhop.data.model.Category
import com.example.trifhop.data.model.Product
import com.example.trifhop.data.repository.ProductRepository
import com.example.trifhop.data.local.CartManager
import com.example.trifhop.data.local.UserPreferences
import com.example.trifhop.data.local.WishlistManager
import com.example.trifhop.ui.components.*
import com.example.trifhop.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    productRepository: ProductRepository,
    cartManager: CartManager
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val wishlistManager = remember { WishlistManager.getInstance(context) }
    val user = remember { userPreferences.getUser() }

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var cartCount by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Parallax offset for hero section
    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstVisibleOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            try {
                val categoriesResult = productRepository.getCategories()
                if (categoriesResult.isSuccess) categories = categoriesResult.getOrNull() ?: emptyList()
                val productsResult = productRepository.getProducts()
                if (productsResult.isSuccess) {
                    products = productsResult.getOrNull() ?: emptyList()
                } else {
                    errorMessage = productsResult.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { cartCount = cartManager.getCartCount() }

    Scaffold(
        topBar = {
            // Glassmorphism TopAppBar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background.copy(
                    alpha = if (firstVisibleIndex > 0 || firstVisibleOffset > 80) 0.95f else 0.0f
                ),
                shadowElevation = if (firstVisibleIndex > 0 || firstVisibleOffset > 80) 4.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Trifhop",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = TrifhopBlue
                        )
                        Text(
                            "Premium Thrift Experience",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box {
                            IconButton(onClick = { navController.navigate("cart") }) {
                                Icon(Icons.Default.ShoppingBag, "Cart",
                                    tint = MaterialTheme.colorScheme.onBackground)
                            }
                            if (cartCount > 0) {
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd).offset(x = (-6).dp, y = 4.dp),
                                    containerColor = TrifhopBlue
                                ) {
                                    Text(if (cartCount > 99) "99+" else cartCount.toString(),
                                        fontSize = 9.sp, color = Color.White)
                                }
                            }                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ─── HERO BANNER ────────────────────────────────────────────────
            item {
                HomeHeroBanner(
                    userName = user?.name ?: "Explorer",
                    onProfileClick = { navController.navigate("profile") },
                    onSearchClick = { navController.navigate("explore") },
                    scrollOffset = if (firstVisibleIndex == 0) firstVisibleOffset else 0
                )
            }

            // ─── STATS ROW ──────────────────────────────────────────────────
            item {
                HomeStatsRow(
                    productCount = products.size,
                    categoryCount = categories.size
                )
            }

            // ─── CATEGORIES ─────────────────────────────────────────────────
            if (categories.isNotEmpty()) {
                item {
                    HomeSectionHeader(
                        title = "Shop by Category",
                        subtitle = "${categories.size} categories"
                    )
                }
                item {
                    CategoryRowAnimated(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategoryClick = { cat ->
                            selectedCategory = if (selectedCategory == cat) null else cat
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ─── PRODUCTS PER CATEGORY ──────────────────────────────────────
            if (isLoading) {
                item {
                    ShimmerProductSection()
                }
                item {
                    ShimmerProductSection()
                }
            } else if (errorMessage != null) {
                item {
                    HomeErrorState(message = errorMessage!!) {
                        scope.launch {
                            isLoading = true; errorMessage = null
                            val r = productRepository.getProducts()
                            if (r.isSuccess) products = r.getOrNull() ?: emptyList()
                            else errorMessage = r.exceptionOrNull()?.message
                            isLoading = false
                        }
                    }
                }
            } else {
                val categoriesToShow = if (selectedCategory != null)
                    listOf(selectedCategory!!) else categories

                categoriesToShow.forEach { category ->
                    val catProducts = products.filter {
                        it.category?.id == category.id || it.categoryId == category.id
                    }
                    if (catProducts.isNotEmpty()) {
                        item(key = "header_${category.id}") {
                            CategorySectionHeader(
                                category = category,
                                count = catProducts.size,
                                onSeeAll = {
                                    navController.navigate("explore?categoryId=${category.id}")
                                }
                            )
                        }
                        item(key = "row_${category.id}") {
                            ProductHorizontalRow(
                                products = catProducts.take(6),
                                wishlistManager = wishlistManager,
                                onProductClick = { navController.navigate("detail/${it.id}") },
                                onAddToCart = {
                                    cartManager.addToCart(it)
                                    cartCount = cartManager.getCartCount()
                                    scope.launch { snackbarHostState.showSnackbar("Dimasukkan ke keranjang") }
                                }
                            )
                        }
                    }
                }

                if (products.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) {
                            EmptyState(
                                icon = Icons.Outlined.Inventory2,
                                title = "Belum Ada Produk",
                                message = "Produk thrift premium akan segera hadir. Stay tuned!",
                                actionText = null
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(88.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  HERO BANNER with Parallax gradient
// ─────────────────────────────────────────────────────────────
@Composable
fun HomeHeroBanner(
    userName: String,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    scrollOffset: Int
) {
    val parallaxShift = (scrollOffset * 0.3f).coerceIn(0f, 60f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .offset(y = (-parallaxShift).dp)
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            TrifhopBlue.copy(alpha = 0.25f),
                            TrifhopBlueDark.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(300f, 0f),
                        radius = 900f
                    )
                )
        )

        // Decorative circle shapes
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .background(
                    TrifhopBlue.copy(alpha = 0.07f),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = 40.dp)
                .background(
                    TrifhopBlueLight.copy(alpha = 0.05f),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Greeting + Avatar Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = getGreeting(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Ready to find your next gem? ✨",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Avatar
                Surface(
                    onClick = onProfileClick,
                    shape = CircleShape,
                    color = TrifhopBlue.copy(alpha = 0.15f),
                    border = BorderStroke(2.dp, TrifhopBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TrifhopBlue
                        )
                    }
                }
            }

            // Search bar tap target
            Surface(
                onClick = onSearchClick,
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Search, null, tint = TrifhopBlue,
                        modifier = Modifier.size(20.dp))
                    Text(
                        "Search thrifted gems…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TrifhopBlue.copy(alpha = 0.12f)
                    ) {
                        Icon(Icons.Default.Tune, null, tint = TrifhopBlue,
                            modifier = Modifier.padding(6.dp).size(16.dp))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  STATS ROW
// ─────────────────────────────────────────────────────────────
@Composable
fun HomeStatsRow(productCount: Int, categoryCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatChip(icon = Icons.Outlined.Inventory2, label = "$productCount items",
            modifier = Modifier.weight(1f))
        StatChip(icon = Icons.Outlined.Category, label = "$categoryCount categories",
            modifier = Modifier.weight(1f))
        StatChip(icon = Icons.Outlined.LocalOffer, label = "Best price",
            modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatChip(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, null, tint = TrifhopBlue, modifier = Modifier.size(14.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  SECTION HEADERS
// ─────────────────────────────────────────────────────────────
@Composable
fun HomeSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(subtitle, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CategorySectionHeader(category: Category, count: Int, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "$count products",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(
            onClick = onSeeAll,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text("See All", color = TrifhopBlue,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold)
            Icon(Icons.Default.ChevronRight, null, tint = TrifhopBlue,
                modifier = Modifier.size(18.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  ANIMATED CATEGORY ROW
// ─────────────────────────────────────────────────────────────
@Composable
fun CategoryRowAnimated(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategoryClick: (Category) -> Unit
) {
    val categoryIcons = mapOf(
        "hoodie" to Icons.Outlined.Checkroom,
        "knitwear" to Icons.Outlined.GridView,
        "vintage" to Icons.Outlined.History,
        "jackets" to Icons.Outlined.Dry,
        "shirts" to Icons.Outlined.ShoppingBag,
        "pants" to Icons.Outlined.Style,
        "accessories" to Icons.Outlined.Watch
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // "All" chip
        item {
            AnimatedCategoryChip(
                label = "All",
                icon = Icons.Outlined.Apps,
                isSelected = selectedCategory == null,
                onClick = { onCategoryClick(categories.firstOrNull() ?: return@AnimatedCategoryChip) }
            )
        }
        items(categories, key = { it.id }) { cat ->
            val icon = categoryIcons[cat.name.lowercase()] ?: Icons.Outlined.Category
            AnimatedCategoryChip(
                label = cat.name,
                icon = icon,
                isSelected = selectedCategory == cat,
                onClick = { onCategoryClick(cat) }
            )
        }
    }
}

@Composable
fun AnimatedCategoryChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) TrifhopBlue else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "chip_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "chip_content"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = bgColor,
        border = if (!isSelected) BorderStroke(1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)) else null,
        shadowElevation = if (isSelected) 4.dp else 0.dp,
        modifier = Modifier.scale(scale)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(18.dp))
            Text(label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  HORIZONTAL PRODUCTS ROW
// ─────────────────────────────────────────────────────────────
@Composable
fun ProductHorizontalRow(
    products: List<Product>,
    wishlistManager: WishlistManager,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products, key = { it.id }) { product ->
            var isInWishlist by remember { mutableStateOf(wishlistManager.isInWishlist(product.id)) }
            PremiumProductCard(
                product = product,
                modifier = Modifier.width(175.dp),
                isInWishlist = isInWishlist,
                onWishlistToggle = { isInWishlist = wishlistManager.toggleWishlist(product.id) },
                onClick = { onProductClick(product) },
                showQuickAdd = product.isAvailable(),
                onQuickAddClick = { onAddToCart(product) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  SHIMMER LOADING SECTION
// ─────────────────────────────────────────────────────────────
@Composable
fun ShimmerProductSection() {
    Column {
        // Header shimmer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ShimmerEffect(Modifier.width(120.dp).height(16.dp).clip(RoundedCornerShape(4.dp)))
                ShimmerEffect(Modifier.width(80.dp).height(11.dp).clip(RoundedCornerShape(4.dp)))
            }
            ShimmerEffect(Modifier.width(60.dp).height(14.dp).clip(RoundedCornerShape(8.dp)))
        }
        // Cards shimmer row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                ShimmerProductCardItem(Modifier.width(175.dp))
            }
        }
    }
}

@Composable
fun ShimmerProductCardItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(290.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            ShimmerEffect(
                Modifier
                    .fillMaxWidth()
                    .height(195.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerEffect(Modifier.fillMaxWidth(0.85f).height(13.dp).clip(RoundedCornerShape(4.dp)))
                ShimmerEffect(Modifier.fillMaxWidth(0.6f).height(11.dp).clip(RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(4.dp))
                ShimmerEffect(Modifier.fillMaxWidth(0.5f).height(15.dp).clip(RoundedCornerShape(4.dp)))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  ERROR STATE
// ─────────────────────────────────────────────────────────────
@Composable
fun HomeErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(Icons.Outlined.WifiOff, null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        Text("Koneksi Bermasalah",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Text(message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = TrifhopBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Coba Lagi")
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  HELPERS
// ─────────────────────────────────────────────────────────────
private fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning ☀️"
        hour < 17 -> "Good Afternoon 🌤️"
        else      -> "Good Evening 🌙"
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM GREETING HEADER - Award Winning Design
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun GreetingHeader(
    userName: String,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hello, $userName!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ready for your next find?",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8)
            )
        }
        
        // Profile Photo dengan border
        Box(
            modifier = Modifier
                .size(56.dp)
                .clickable(onClick = onProfileClick)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                border = BorderStroke(2.dp, Color(0xFF137FEC).copy(alpha = 0.3f)),
                color = Color(0xFF1E1E1E)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "U",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF137FEC)
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM SEARCH BAR - Glassmorphism Effect
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun SearchBarSection(onSearchClick: () -> Unit) {
    Card(
        onClick = onSearchClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF137FEC),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Search thrifted gems...",
                fontSize = 15.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

// Category List dengan Material Icons
@Composable
fun CategoryListWithIcons(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategoryClick: (Category) -> Unit
) {
    // Mapping category names to icons
    val categoryIcons = mapOf(
        "Hoodie" to Icons.Outlined.Checkroom,
        "Knitwear" to Icons.Outlined.GridView,
        "Vintage" to Icons.Outlined.History,
        "Jackets" to Icons.Outlined.Dry,
        "Shirts" to Icons.Outlined.ShoppingBag,
        "Pants" to Icons.Outlined.Style,
        "Accessories" to Icons.Outlined.Watch
    )
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryChipWithIcon(
                category = category,
                icon = categoryIcons[category.name] ?: Icons.Outlined.Category,
                isSelected = selectedCategory == category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM CATEGORY CHIP - Smooth Material Design
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun CategoryChipWithIcon(
    category: Category,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF137FEC) else Color(0xFF1E1E1E)
        ),
        border = if (!isSelected) BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)) else null,
        modifier = Modifier.height(48.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 3.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = category.name,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF94A3B8)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM CATEGORY HEADER - Clean Layout
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun CategoryHeader(
    category: Category,
    productCount: Int,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = category.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "$productCount items available",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )
        }
        TextButton(
            onClick = onSeeAllClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF137FEC)
            )
        ) {
            Text(
                text = "See All",
                color = Color(0xFF137FEC),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF137FEC),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ProductGridWithWishlist(
    products: List<Product>,
    wishlistManager: WishlistManager,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCardWithWishlist(
                product = product,
                wishlistManager = wishlistManager,
                onClick = { onProductClick(product) },
                onAddToCart = { onAddToCart(product) }
            )
        }
    }
}

@Composable
fun ProductCardWithWishlist(
    product: Product,
    wishlistManager: WishlistManager,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    var isInWishlist by remember { mutableStateOf(wishlistManager.isInWishlist(product.id)) }
    
    // 🎨 PREMIUM PRODUCT CARD WITH PIXEL-PERFECT CONSISTENCY
    com.example.trifhop.ui.components.PremiumProductCard(
        product = product,
        modifier = Modifier.width(180.dp),
        isInWishlist = isInWishlist,
        onWishlistToggle = {
            isInWishlist = wishlistManager.toggleWishlist(product.id)
        },
        onClick = onClick,
        showQuickAdd = product.isAvailable(),
        onQuickAddClick = onAddToCart
    )
}
