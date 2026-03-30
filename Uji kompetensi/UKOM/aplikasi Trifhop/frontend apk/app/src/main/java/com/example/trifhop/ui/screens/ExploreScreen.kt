package com.example.trifhop.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trifhop.data.model.Category
import com.example.trifhop.data.model.Product
import com.example.trifhop.data.repository.ProductRepository
import com.example.trifhop.data.local.CartManager
import com.example.trifhop.data.local.WishlistManager
import com.example.trifhop.ui.components.*
import com.example.trifhop.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    productRepository: ProductRepository,
    cartManager: CartManager,
    categoryId: Int? = null
) {
    val context = LocalContext.current
    val wishlistManager = remember { WishlistManager.getInstance(context) }
    
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var filteredProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    // Load data
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            try {
                // Load categories
                val categoriesResult = productRepository.getCategories()
                if (categoriesResult.isSuccess) {
                    categories = categoriesResult.getOrNull() ?: emptyList()
                    
                    // Set selected category if categoryId provided
                    if (categoryId != null) {
                        selectedCategory = categories.find { it.id == categoryId }
                    }
                }
                
                // Load products
                val productsResult = productRepository.getProducts()
                if (productsResult.isSuccess) {
                    products = productsResult.getOrNull() ?: emptyList()
                    filteredProducts = products
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
    
    // Filter products
    LaunchedEffect(searchQuery, selectedCategory) {
        filteredProducts = products.filter { product ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
            }
            
            val matchesCategory = if (selectedCategory == null) {
                true
            } else {
                product.category?.id == selectedCategory?.id ||
                product.categoryId == selectedCategory?.id
            }
            
            matchesSearch && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column {
                                Text(
                                    "Explore",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                AnimatedContent(
                                    targetState = "${filteredProducts.size} products",
                                    transitionSpec = {
                                        slideInVertically { -it } + fadeIn() togetherWith
                                            slideOutVertically { it } + fadeOut()
                                    },
                                    label = "count_anim"
                                ) { count ->
                                    Text(
                                        count,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        // Filter button
                        Surface(
                            onClick = { /* TODO: filter bottom sheet */ },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedCategory != null) TrifhopBlue else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Tune, "Filter",
                                    tint = if (selectedCategory != null) Color.White
                                           else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Search field
                    ExploreSearchField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClear = { searchQuery = "" }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category filter chips
            if (categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        ExploreFilterChip(
                            label = "All",
                            isSelected = selectedCategory == null,
                            onClick = { selectedCategory = null }
                        )
                    }
                    items(categories, key = { it.id }) { cat ->
                        ExploreFilterChip(
                            label = cat.name,
                            isSelected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = if (selectedCategory == cat) null else cat
                            }
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    thickness = 1.dp
                )
            }

            // Products grid
            when {
                isLoading -> {
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

                filteredProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(40.dp)
                        ) {
                            // Animated empty icon
                            val pulse by rememberInfiniteTransition(label = "pulse")
                                .animateFloat(
                                    initialValue = 0.9f,
                                    targetValue = 1.1f,
                                    animationSpec = infiniteRepeatable(
                                        tween(1000),
                                        RepeatMode.Reverse
                                    ),
                                    label = "pulse_scale"
                                )
                            Icon(
                                Icons.Outlined.SearchOff, null,
                                tint = TrifhopBlue.copy(alpha = 0.3f),
                                modifier = Modifier.size(90.dp).scale(pulse)
                            )
                            Text(
                                if (searchQuery.isNotBlank()) "Produk Tidak Ditemukan"
                                else "Belum Ada Produk di Kategori Ini",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                if (searchQuery.isNotBlank())
                                    "Coba kata kunci lain atau hapus filter"
                                else "Produk akan segera tersedia",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            if (searchQuery.isNotBlank() || selectedCategory != null) {
                                Button(
                                    onClick = { searchQuery = ""; selectedCategory = null },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TrifhopBlue
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Clear, null,
                                        modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Hapus Filter")
                                }
                            }
                        }
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(filteredProducts, key = { _, p -> p.id }) { index, product ->
                            var isInWishlist by remember { mutableStateOf(wishlistManager.isInWishlist(product.id)) }
                            // Stagger entrance animation
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(
                                    tween(300, delayMillis = (index % 6) * 50)
                                ) + slideInVertically(
                                    tween(300, delayMillis = (index % 6) * 50)
                                ) { it / 4 }
                            ) {
                                PremiumProductCard(
                                    product = product,
                                    modifier = Modifier.fillMaxWidth(),
                                    isInWishlist = isInWishlist,
                                    onWishlistToggle = {
                                        isInWishlist = wishlistManager.toggleWishlist(product.id)
                                    },
                                    onClick = { navController.navigate("detail/${product.id}") },
                                    showQuickAdd = product.isAvailable(),
                                    onQuickAddClick = { cartManager.addToCart(product) }
                                )
                            }
                        }
                        // Bottom padding
                        item(span = { GridItemSpan(2) }) {
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  SEARCH FIELD
// ─────────────────────────────────────────────────────────────
@Composable
fun ExploreSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) TrifhopBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        label = "search_border"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.Search, null,
                tint = if (isFocused) TrifhopBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                ),
                singleLine = true,
                decorationBox = { inner ->
                    Box(Modifier.padding(vertical = 12.dp)) {
                        if (query.isEmpty()) {
                            Text(
                                "Cari produk thrift premium…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        inner()
                    }
                }
            )
            AnimatedVisibility(visible = query.isNotBlank(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()) {
                IconButton(onClick = onClear, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  FILTER CHIP
// ─────────────────────────────────────────────────────────────
@Composable
fun ExploreFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        },
        leadingIcon = if (isSelected) {
            { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TrifhopBlue,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            selectedBorderColor = TrifhopBlue,
            enabled = true,
            selected = isSelected
        ),
        modifier = Modifier.scale(scale)
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM SEARCH BAR - Glassmorphism with Active Search
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF137FEC),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                ),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Search thrifted gems...",
                                fontSize = 15.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        innerTextField()
                    }
                },
                singleLine = true
            )
            
            if (query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM CATEGORY FILTER ROW - Smooth Chips
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun CategoryFilterRow(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategoryClick: (Category) -> Unit
) {
    Column {
        Text(
            text = "Categories",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategoryClick(category) },
                    label = {
                        Text(
                            text = category.name,
                            fontSize = 14.sp,
                            fontWeight = if (selectedCategory == category) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = if (selectedCategory == category) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF137FEC),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E1E1E),
                        labelColor = Color(0xFF94A3B8)
                    ),
                    border = if (selectedCategory != category) {
                        androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    } else null
                )
            }
        }
    }
}

@Composable
fun ProductsGridWithWishlist(
    products: List<Product>,
    wishlistManager: WishlistManager,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductGridCardWithWishlist(
                product = product,
                wishlistManager = wishlistManager,
                onClick = { onProductClick(product) },
                onAddToCart = { onAddToCart(product) }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//   PREMIUM PRODUCT GRID CARD - Pixel-Perfect Consistency
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
fun ProductGridCardWithWishlist(
    product: Product,
    wishlistManager: WishlistManager,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    var isInWishlist by remember { mutableStateOf(wishlistManager.isInWishlist(product.id)) }
    
    // 🎨 PREMIUM PRODUCT CARD - UNIVERSAL & CONSISTENT
    com.example.trifhop.ui.components.PremiumProductCard(
        product = product,
        modifier = Modifier.fillMaxWidth(),
        isInWishlist = isInWishlist,
        onWishlistToggle = {
            isInWishlist = wishlistManager.toggleWishlist(product.id)
        },
        onClick = onClick,
        showQuickAdd = product.isAvailable(),
        onQuickAddClick = onAddToCart
    )
}
