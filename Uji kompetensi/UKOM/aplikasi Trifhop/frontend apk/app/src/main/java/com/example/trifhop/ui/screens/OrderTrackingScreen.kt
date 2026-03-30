package com.example.trifhop.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trifhop.data.local.UserPreferences
import com.example.trifhop.data.model.Order
import com.example.trifhop.data.repository.OrderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    navController: NavController,
    orderRepository: OrderRepository,
    userPreferences: UserPreferences,
    orderId: Int
) {
    var order by remember { mutableStateOf<Order?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val formatter = remember {
        NumberFormat.getNumberInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
    }
    fun fmt(amount: Double) = "Rp ${formatter.format(amount.toLong())}"
    val dateFormatter = remember {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    }

    LaunchedEffect(orderId) {
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
                
                val result = orderRepository.getOrderById(token, orderId)
                if (result.isSuccess) {
                    order = result.getOrNull()
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

    // Auto-polling: refresh status pesanan setiap 10 detik
    LaunchedEffect(orderId, "polling") {
        while (true) {
            delay(10_000L)
            val token = userPreferences.getToken() ?: break
            try {
                val result = orderRepository.getOrderById(token, orderId)
                result.getOrNull()?.let { updated -> order = updated }
            } catch (_: Exception) {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lacak Pesanan",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212),
                    titleContentColor = Color(0xFF1A1A2E)
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF137FEC)
                        )
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
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan",
                            fontSize = 16.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF137FEC)
                            )
                        ) {
                            Text("Kembali")
                        }
                    }
                }
                
                order != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Invoice Info
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "No. Invoice",
                                                fontSize = 12.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                            Text(
                                                text = order!!.invoiceCode,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        
                                        StatusBadge(status = order!!.status)
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFF2D3748))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Tanggal Pesanan",
                                            fontSize = 14.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Text(
                                            text = try {
                                                dateFormatter.format(
                                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                                        .parse(order!!.orderedAt) ?: Date()
                                                )
                                            } catch (e: Exception) {
                                                order!!.orderedAt
                                            },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Order Status Stepper
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Status Pesanan",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    OrderStatusStepper(currentStatus = order!!.status)
                                }
                            }
                        }
                        
                        // Shipping Info
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalShipping,
                                            contentDescription = null,
                                            tint = Color(0xFF137FEC),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Informasi Pengiriman",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFF2D3748))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    InfoRow(label = "Nama Penerima", value = order!!.customerName)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    InfoRow(label = "No. Telepon", value = order!!.customerPhone)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    InfoRow(label = "Alamat", value = order!!.shippingAddress)
                                }
                            }
                        }
                        
                        // Order Items
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingBag,
                                            contentDescription = null,
                                            tint = Color(0xFF137FEC),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Produk (${order!!.details?.size ?: 0})",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFF2D3748))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    order!!.details?.forEach { detail ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(modifier = Modifier.weight(1f)) {
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
                                                            .size(60.dp)
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                    
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    
                                                    Column {
                                                        Text(
                                                            text = product.name,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = Color.White,
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "x${detail.quantity}",
                                                            fontSize = 12.sp,
                                                            color = Color(0xFF94A3B8)
                                                        )
                                                    }
                                                }
                                            }
                                            
                                            Text(
                                                text = fmt(detail.priceAtPurchase * detail.quantity),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF137FEC)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFF2D3748))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Total Pembayaran",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = fmt(order!!.totalPrice),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF137FEC)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusStepper(currentStatus: String) {
    val steps = listOf(
        StepInfo("pending", "Pending", Icons.Default.Schedule),
        StepInfo("packed", "Packed", Icons.Default.Inventory),
        StepInfo("sent", "Sent", Icons.Default.LocalShipping),
        StepInfo("finished", "Delivered", Icons.Default.CheckCircle)
    )
    
    val currentIndex = steps.indexOfFirst { it.status == currentStatus.lowercase() }
    
    // Calculate progress (0.0 to 1.0)
    val progress = if (currentIndex < 0) 0f else (currentIndex.toFloat() / (steps.size - 1))
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Horizontal Stepper with Progress Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            // Background Line (full width)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 18.dp)
                    .background(
                        color = Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            
            // Progress Line (partial width based on progress)
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(3.dp)
                    .align(Alignment.TopStart)
                    .offset(y = 18.dp)
                    .background(
                        color = Color(0xFF137FEC),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            
            // Step Circles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                steps.forEachIndexed { index, step ->
                    val isActive = index <= currentIndex
                    val isCurrentStep = index == currentIndex
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(80.dp)
                    ) {
                        // Circle with Icon
                        Box(
                            modifier = Modifier.size(if (isCurrentStep) 44.dp else 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = if (isActive) Color(0xFF137FEC) else Color(0xFFE2E8F0),
                                shadowElevation = if (isCurrentStep) 4.dp else 0.dp
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = step.icon,
                                        contentDescription = null,
                                        tint = if (isActive) Color.White else Color.Gray,
                                        modifier = Modifier.size(if (isCurrentStep) 22.dp else 20.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Label
                        Text(
                            text = step.label,
                            fontSize = if (isCurrentStep) 13.sp else 12.sp,
                            fontWeight = if (isCurrentStep) FontWeight.Bold else FontWeight.Medium,
                            color = if (isActive) Color(0xFF0F172A) else Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        
        // Current Status Description (Optional)
        if (currentIndex >= 0 && currentIndex < steps.size) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF137FEC).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF137FEC),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = when (currentStatus.lowercase()) {
                            "pending" -> "Pesanan Anda sedang menunggu konfirmasi dari penjual"
                            "packed" -> "Pesanan Anda sedang dikemas dengan rapi"
                            "sent" -> "Pesanan Anda sedang dalam perjalanan"
                            "finished" -> "Pesanan Anda telah sampai. Terima kasih!"
                            else -> "Status pesanan tidak diketahui"
                        },
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

data class StepInfo(
    val status: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
