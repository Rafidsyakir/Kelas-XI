package com.example.trifhop.ui.screens

import androidx.compose.foundation.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trifhop.data.local.UserPreferences
import com.example.trifhop.data.model.Order
import com.example.trifhop.data.repository.OrderRepository
import com.example.trifhop.ui.navigation.Screen
import com.example.trifhop.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    orderRepository: OrderRepository,
    userPreferences: UserPreferences
) {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var filteredOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("all") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    
    val scope = rememberCoroutineScope()
    val formatter = remember {
        NumberFormat.getNumberInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
    }
    val dateFormatter = remember {
        SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    }

    // Load orders
    LaunchedEffect(refreshTrigger) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrBlank()) {
                    errorMessage = "Silakan login terlebih dahulu"
                    isLoading = false
                    return@launch
                }
                
                val result = orderRepository.getUserOrders(token)
                if (result.isSuccess) {
                    orders = result.getOrNull() ?: emptyList()
                    filteredOrders = orders
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Gagal memuat pesanan"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Terjadi kesalahan"
            } finally {
                isLoading = false
            }
        }
    }
    
    // Filter orders when filter changes
    LaunchedEffect(selectedFilter, orders) {
        filteredOrders = when (selectedFilter) {
            "all" -> orders
            "pending" -> orders.filter { it.status.lowercase() == "pending" }
            "packed" -> orders.filter { it.status.lowercase() == "packed" }
            "sent" -> orders.filter { it.status.lowercase() == "sent" }
            "finished" -> orders.filter { it.status.lowercase() == "finished" }
            "cancelled" -> orders.filter { it.status.lowercase() == "cancelled" }
            else -> orders
        }
    }

    // Auto-refresh status pesanan setiap 30 detik
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            refreshTrigger++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Riwayat Pesanan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { refreshTrigger++ }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Chips
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OrderFilterChip(
                        label = "Semua",
                        isSelected = selectedFilter == "all",
                        onClick = { selectedFilter = "all" }
                    )
                    OrderFilterChip(
                        label = "Menunggu",
                        isSelected = selectedFilter == "pending",
                        onClick = { selectedFilter = "pending" }
                    )
                    OrderFilterChip(
                        label = "Dikemas",
                        isSelected = selectedFilter == "packed",
                        onClick = { selectedFilter = "packed" }
                    )
                    OrderFilterChip(
                        label = "Dikirim",
                        isSelected = selectedFilter == "sent",
                        onClick = { selectedFilter = "sent" }
                    )
                    OrderFilterChip(
                        label = "Selesai",
                        isSelected = selectedFilter == "finished",
                        onClick = { selectedFilter = "finished" }
                    )
                    OrderFilterChip(
                        label = "Dibatalkan",
                        isSelected = selectedFilter == "cancelled",
                        onClick = { selectedFilter = "cancelled" }
                    )
                }
            }
            
            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    isLoading -> {
                        // Shimmer Loading State
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(5) {
                                ShimmerOrderCard()
                            }
                        }
                    }
                    
                    errorMessage != null -> {
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
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TrifhopBlue
                                )
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                    
                    filteredOrders.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = if (selectedFilter == "all") "Belum Ada Pesanan" else "Tidak Ada Pesanan",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedFilter == "all") 
                                    "Pesanan yang Anda buat akan muncul di sini" 
                                else 
                                    "Tidak ada pesanan dengan status ini",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            if (selectedFilter == "all") {
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { navController.navigate(Screen.Home.route) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TrifhopBlue
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Mulai Belanja", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                    
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredOrders) { order ->
                                OrderCard(
                                    order = order,
                                    formatter = formatter,
                                    dateFormatter = dateFormatter,
                                    onOrderClick = {
                                        navController.navigate(Screen.OrderTracking.createRoute(order.id))
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

@Composable
fun OrderCard(
    order: Order,
    formatter: NumberFormat,
    dateFormatter: SimpleDateFormat,
    onOrderClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Invoice & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.invoiceCode,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = try {
                            dateFormatter.format(
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    .parse(order.orderedAt) ?: Date()
                            )
                        } catch (e: Exception) {
                            order.orderedAt
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    StatusBadgeSmall(status = order.status)
                    Spacer(modifier = Modifier.height(6.dp))
                    PaymentStatusBadgeSmall(status = order.paymentStatus)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(12.dp))
            
            // Products Preview (show first 2 items)
            order.details?.take(2)?.forEach { detail ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        detail.product?.let { product ->
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = if (product.imageUrl.startsWith("http")) {
                                        product.imageUrl
                                    } else {
                                        "${com.example.trifhop.data.network.ApiConfig.BASE_URL}storage/$product.imageUrl"
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "x${detail.quantity}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            // Show "more items" if there are more than 2
            if ((order.details?.size ?: 0) > 2) {
                Text(
                    text = "+${(order.details?.size ?: 0) - 2} produk lainnya",
                    fontSize = 12.sp,
                    color = TrifhopBlue,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(12.dp))
            
            // Total & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Pembayaran",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Rp ${formatter.format(order.totalPrice.toLong())}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrifhopBlue
                    )
                }
                
                Button(
                    onClick = onOrderClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrifhopBlue
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Lihat Detail",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentStatusBadgeSmall(status: String?) {
    val normalized = status?.uppercase(Locale.getDefault()) ?: "UNPAID"
    val (bgColor, textColor, label) = when (normalized) {
        "SUCCESS", "PAID", "SETTLED" -> Triple(Color(0xFFD1FAE5), Color(0xFF059669), "Dibayar")
        "EXPIRED" -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), "Kedaluwarsa")
        "PENDING", "UNPAID" -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "Belum Bayar")
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF6B7280), normalized)
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun ShimmerOrderCard() {
    val shimmer = rememberInfiniteTransition(label = "shimmer")
    val alpha by shimmer.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "alpha"
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier
                        .height(18.dp)
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
                )
                Box(
                    Modifier
                        .height(18.dp)
                        .width(80.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
                )
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            Spacer(Modifier.height(12.dp))
            // Product row skeleton
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Box(
                        Modifier
                            .height(14.dp)
                            .fillMaxWidth(0.6f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        Modifier
                            .height(12.dp)
                            .width(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.6f))
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            Spacer(Modifier.height(12.dp))
            // Total row skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .height(20.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(TrifhopBlue.copy(alpha = alpha * 0.4f))
                )
                Box(
                    Modifier
                        .height(36.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TrifhopBlue.copy(alpha = alpha * 0.3f))
                )
            }
        }
    }
}

@Composable
fun OrderFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) TrifhopBlue else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatusBadgeSmall(status: String) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "pending" -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "Menunggu")
        "packed" -> Triple(Color(0xFFDCEFFF), Color(0xFF2563EB), "Dikemas")
        "sent" -> Triple(Color(0xFFDBEAFE), Color(0xFF1D4ED8), "Dikirim")
        "finished" -> Triple(Color(0xFFD1FAE5), Color(0xFF059669), "Selesai")
        "cancelled" -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), "Dibatalkan")
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF6B7280), status)
    }
    
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
