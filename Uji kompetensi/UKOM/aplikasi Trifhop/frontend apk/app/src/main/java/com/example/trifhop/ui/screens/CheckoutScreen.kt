package com.example.trifhop.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trifhop.data.local.CartManager
import com.example.trifhop.data.local.UserPreferences
import com.example.trifhop.data.model.Order
import com.example.trifhop.data.model.OrderProduct
import com.example.trifhop.data.model.OrderCreateResult
import com.example.trifhop.data.repository.OrderRepository
import com.example.trifhop.ui.navigation.Screen
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import org.json.JSONObject
import org.json.JSONException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartManager: CartManager,
    orderRepository: OrderRepository,
    userPreferences: UserPreferences
) {
    val cartItems = remember { cartManager.getCartItems() }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var shippingAddress by remember { mutableStateOf("") }
    var selectedCourier by remember { mutableStateOf("Regular") }
    var isProcessing by remember { mutableStateOf(false) }
    var isCheckingStatus by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showWaitingPaymentDialog by remember { mutableStateOf(false) }
    var showMidtransPayment by remember { mutableStateOf(false) }
    var checkoutPaymentUrl by remember { mutableStateOf<String?>(null) }
    var createdOrderId by remember { mutableStateOf<Int?>(null) }
    
    val scope = rememberCoroutineScope()
    val formatter = remember {
        NumberFormat.getNumberInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
    }
    fun fmt(amount: Double) = "Rp ${formatter.format(amount.toLong())}"
    
    // Calculate totals
    val subtotal = cartItems.sumOf { it.getTotalPrice() }
    val shippingFee = when (selectedCourier) {
        "Regular" -> 10000.0
        "Express" -> 25000.0
        else -> 10000.0
    }
    val total = subtotal + shippingFee

    // If cart is empty, redirect back
    LaunchedEffect(cartItems.isEmpty()) {
        if (cartItems.isEmpty()) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout",
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
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF121212),
        bottomBar = {
            // Sticky Summary Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1A1A2E),
                shadowElevation = 8.dp
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
                                text = "Total Pembayaran",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = fmt(total),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF137FEC)
                            )
                        }
                        
                        Button(
                            onClick = {
                                // Validate inputs
                                when {
                                    customerName.isBlank() -> {
                                        errorMessage = "Nama harus diisi"
                                    }
                                    customerPhone.isBlank() -> {
                                        errorMessage = "Nomor telepon harus diisi"
                                    }
                                    shippingAddress.isBlank() -> {
                                        errorMessage = "Alamat pengiriman harus diisi"
                                    }
                                    else -> {
                                        // 🔧 Validate cart before checkout
                                        val cleanupCount = cartManager.cleanupInvalidProducts()
                                        if (cleanupCount > 0) {
                                            errorMessage = "⚠️ Ditemukan $cleanupCount produk invalid yang dihapus dari keranjang"
                                        } else {
                                            val (isCartValid, validationError) = cartManager.validateCart()
                                            if (!isCartValid) {
                                                errorMessage = validationError ?: "Data keranjang tidak valid"
                                            } else if (customerName.isBlank()) {
                                                errorMessage = "Nama pelanggan harus diisi"
                                            } else if (customerPhone.isBlank()) {
                                                errorMessage = "Nomor telepon harus diisi"
                                            } else if (shippingAddress.isBlank()) {
                                                errorMessage = "Alamat pengiriman harus diisi"
                                            } else {
                                                // Proceed with checkout
                                                scope.launch {
                                                    isProcessing = true
                                                    errorMessage = null
                                                    try {
                                                        val token = userPreferences.getToken()
                                                        if (token.isNullOrBlank()) {
                                                            errorMessage = "Silakan login terlebih dahulu"
                                                            return@launch
                                                        }
                                                        val result = orderRepository.createOrder(
                                                            token = token,
                                                            customerName = customerName.trim(),
                                                            customerPhone = customerPhone.trim(),
                                                            shippingAddress = shippingAddress.trim(),
                                                            products = cartItems.map { item ->
                                                                OrderProduct(id = item.product.id, quantity = item.quantity)
                                                            },
                                                            shippingFee = shippingFee.toInt()
                                                        )
                                                        if (result.isSuccess) {
                                                            val orderResult = result.getOrNull()
                                                            val orderId = orderResult?.order?.id
                                                            if (orderId == null) {
                                                                errorMessage = "Order ID tidak ditemukan"
                                                                return@launch
                                                            }
                                                            val checkoutResult = orderRepository.createCheckoutInvoice(token = token, orderId = orderId)
                                                            if (checkoutResult.isSuccess) {
                                                                val paymentUrl = checkoutResult.getOrNull()?.paymentUrlOrInvoiceUrl
                                                                if (!paymentUrl.isNullOrBlank()) {
                                                                    checkoutPaymentUrl = paymentUrl
                                                                    showMidtransPayment = true
                                                                } else {
                                                                    errorMessage = "Payment URL tidak tersedia"
                                                                }
                                                            } else {
                                                                errorMessage = checkoutResult.exceptionOrNull()?.message ?: "Gagal membuat pembayaran"
                                                            }
                                                        } else {
                                                            errorMessage = result.exceptionOrNull()?.message ?: "Gagal membuat pesanan"
                                                        }
                                                    } catch (e: Exception) {
                                                        errorMessage = "Error: ${e.message}"
                                                    } finally {
                                                        isProcessing = false
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isProcessing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF137FEC),
                                disabledContainerColor = Color(0xFF94A3B8)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .widthIn(min = 140.dp)
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF1E1E2E),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Bayar Sekarang",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Message
            errorMessage?.let { error ->
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF3D1515),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFFF6B6B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                fontSize = 14.sp,
                                color = Color(0xFFFF6B6B)
                            )
                        }
                    }
                }
            }
            
            // Order Items
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                text = "Produk (${cartItems.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFF2D3748))
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = if (item.product.imageUrl.startsWith("http")) {
                                                item.product.imageUrl
                                            } else {
                                                "${com.example.trifhop.data.network.ApiConfig.BASE_URL}storage/${item.product.imageUrl}"
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
                                            text = item.product.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "x${item.quantity}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                                
                                Text(
                                    text = fmt(item.getTotalPrice()),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF137FEC)
                                )
                            }
                        }
                    }
                }
            }
            
            // Customer Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF137FEC),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Informasi Penerima",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Nama Lengkap", color = Color(0xFF94A3B8)) },
                            placeholder = { Text("Masukkan nama lengkap") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF137FEC),
                                unfocusedBorderColor = Color(0xFF2D3748)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Nomor Telepon", color = Color(0xFF94A3B8)) },
                            placeholder = { Text("08xxxxxxxxxx") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF137FEC),
                                unfocusedBorderColor = Color(0xFF2D3748)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = shippingAddress,
                            onValueChange = { shippingAddress = it },
                            label = { Text("Alamat Lengkap", color = Color(0xFF94A3B8)) },
                            placeholder = { Text("Jalan, Kelurahan, Kecamatan, Kota") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF137FEC),
                                unfocusedBorderColor = Color(0xFF2D3748)
                            )
                        )
                    }
                }
            }
            
            // Courier Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
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
                                text = "Metode Pengiriman",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        CourierOption(
                            title = "Regular",
                            description = "3-5 hari kerja",
                            price = 10000.0,
                            isSelected = selectedCourier == "Regular",
                            onSelect = { selectedCourier = "Regular" },
                            formatter = formatter
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        CourierOption(
                            title = "Express",
                            description = "1-2 hari kerja",
                            price = 25000.0,
                            isSelected = selectedCourier == "Express",
                            onSelect = { selectedCourier = "Express" },
                            formatter = formatter
                        )
                    }
                }
            }
            
            // Payment Method
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null,
                                tint = Color(0xFF137FEC),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Metode Pembayaran",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF137FEC)),
                            color = Color(0xFF1A2D4A)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = Color(0xFF137FEC),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Midtrans Snap Payment",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Virtual Account, E-Wallet (Sandbox), QRIS",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Order Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ringkasan Pembayaran",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFF2D3748))
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Subtotal Produk",
                                fontSize = 14.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = fmt(subtotal),
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Biaya Pengiriman",
                                fontSize = 14.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = fmt(shippingFee),
                                fontSize = 14.sp,
                                color = Color.White
                            )
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
                                text = "Total",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = fmt(total),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF137FEC)
                            )
                        }
                    }
                }
            }
            
            // Bottom Spacing for sticky bar
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // TAHAP 2: Open payment URL in browser when available
    if (showMidtransPayment && !checkoutPaymentUrl.isNullOrBlank()) {
        val context = LocalContext.current
        LaunchedEffect(checkoutPaymentUrl) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(checkoutPaymentUrl))
                context.startActivity(intent)
                showMidtransPayment = false
                showWaitingPaymentDialog = true
            } catch (e: Exception) {
                errorMessage = "Gagal membuka URL pembayaran: ${e.message}"
                showMidtransPayment = false
            }
        }
    }

    if (showWaitingPaymentDialog) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = Color(0xFF1A1A2E),
            title = {
                Text(
                    text = "Waiting for Payment",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Pembayaran belum selesai. Anda bisa lanjutkan pembayaran atau cek status terbaru.",
                    color = Color(0xFF94A3B8)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val orderId = createdOrderId
                        if (orderId == null) {
                            errorMessage = "Order ID tidak ditemukan"
                            return@Button
                        }

                        scope.launch {
                            isCheckingStatus = true
                            try {
                                val token = userPreferences.getToken()
                                if (token.isNullOrBlank()) {
                                    errorMessage = "Sesi login habis. Silakan login ulang."
                                    return@launch
                                }

                                val statusResult = orderRepository.checkPaymentStatus(token, orderId)
                                if (statusResult.isSuccess) {
                                    val latestOrder = statusResult.getOrNull()
                                    val isPaid = latestOrder?.paymentStatus.equals("SUCCESS", ignoreCase = true) ||
                                        latestOrder?.status.equals("finished", ignoreCase = true)
                                    
                                    // ✅ FIX: Clear cart immediately when payment is confirmed
                                    if (isPaid) {
                                        cartManager.clearCart()
                                        showWaitingPaymentDialog = false
                                        showSuccessDialog = true
                                    } else {
                                        errorMessage = "Pembayaran masih menunggu. Silakan selesaikan pembayaran di halaman Midtrans."
                                    }
                                } else {
                                    errorMessage = statusResult.exceptionOrNull()?.message
                                        ?: "Gagal cek status pembayaran"
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Terjadi kesalahan saat cek status"
                            } finally {
                                isCheckingStatus = false
                            }
                        }
                    },
                    enabled = !isCheckingStatus,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137FEC))
                ) {
                    if (isCheckingStatus) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Cek Status Pembayaran")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showWaitingPaymentDialog = false
                        showMidtransPayment = true
                    }
                ) {
                    Text("Lanjutkan Pembayaran", color = Color(0xFF94A3B8))
                }
            }
        )
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        Dialog(onDismissRequest = { }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Pesanan Berhasil!",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pesanan Anda telah berhasil dibuat dan akan segera diproses.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            navController.navigate(Screen.OrderHistory.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137FEC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lihat Pesanan Saya", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kembali ke Beranda", color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }
}

@Composable
fun CourierOption(
    title: String,
    description: String,
    price: Double,
    isSelected: Boolean,
    onSelect: () -> Unit,
    formatter: NumberFormat
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF137FEC) else Color(0xFF2D3748)
        ),
        color = if (isSelected) Color(0xFF1A2D4A) else Color(0xFF1E1E2E)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF137FEC)
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            
            Text(
                text = "Rp ${formatter.format(price.toLong())}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF137FEC)
            )
        }
    }
}

// TAHAP 2: Removed WebView dialog - using Intent.ACTION_VIEW instead for security

@Composable
fun PaymentOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF137FEC) else Color(0xFF2D3748)
        ),
        color = if (isSelected) Color(0xFF1A2D4A) else Color(0xFF1E1E2E)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF137FEC)
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

// Helper function to extract error details from error messages
fun extractErrorDetails(errorMessage: String): String {
    // === Try to parse JSON response ===
    return try {
        // Check if it looks like JSON
        if (!errorMessage.contains("{") || !errorMessage.contains("}")) {
            // Not JSON, return as-is
            return errorMessage.take(200)
        }
        
        // Extract JSON part
        val jsonStart = errorMessage.indexOf("{")
        if (jsonStart < 0) return errorMessage.take(200)
        
        val jsonPart = try {
            errorMessage.substring(jsonStart)
        } catch (e: Exception) {
            return errorMessage.take(200)
        }
        
        val json = JSONObject(jsonPart)
        
        // === Priority 1: Check "error" field (specific error from backend) ===
        if (json.has("error") && !json.isNull("error")) {
            val error = json.getString("error")
            if (error.isNotEmpty()) return error
        }
        
        // === Priority 2: Check "message" field ===
        if (json.has("message") && !json.isNull("message")) {
            val message = json.getString("message")
            if (message.isNotEmpty() && message != "Failed") return message
        }
        
        // === Priority 3: Check "data" field ===
        if (json.has("data") && !json.isNull("data")) {
            val data = json.get("data")
            if (data is String && data.isNotEmpty()) return data
        }
        
        // === Fallback ===
        errorMessage.take(200)
        
    } catch (e: JSONException) {
        // === JSON parsing failed - try regex extraction ===
        try {
            val jsonRegex = "\"(?:message|error)\"\\s*:\\s*\"([^\"]+)\"".toRegex()
            val jsonMatch = jsonRegex.find(errorMessage)
            if (jsonMatch != null) {
                return jsonMatch.groupValues.getOrNull(1) ?: errorMessage.take(200)
            }
        } catch (e: Exception) {
            // Ignore regex errors
        }
        
        errorMessage.take(200)
    } catch (e: Exception) {
        // === Generic exception ===
        errorMessage.take(200)
    }
}

/**
 * Extract specific error information from order creation failure
 * Used to show user-friendly error messages in CheckoutScreen
 * 
 * Handles various error formats:
 * - JSON responses with "error" field (specific error from backend)
 * - JSON responses with "message" field
 * - HTTP status codes
 * - Plain text error messages
 * - Network errors
 */
fun extractOrderErrorMessage(errorMsg: String): String {
    // === Try JSON parsing first ===
    return try {
        // Check if it looks like JSON
        if (errorMsg.contains("{") && errorMsg.contains("}")) {
            val jsonStart = errorMsg.indexOf("{")
            if (jsonStart >= 0) {
                val jsonPart = try {
                    errorMsg.substring(jsonStart)
                } catch (e: Exception) {
                    null
                }
                
                if (jsonPart != null) {
                    try {
                        val json = JSONObject(jsonPart)
                        
                        // === Priority 1: Check "error" field (specific error from backend) ===
                        if (json.has("error") && !json.isNull("error")) {
                            val error = json.getString("error")
                            if (error.isNotEmpty()) {
                                // Log for debugging
                                android.util.Log.d("CheckoutScreen", "✅ Extracted specific error: $error")
                                return error
                            }
                        }
                        
                        // === Priority 2: Check "message" field ===
                        if (json.has("message") && !json.isNull("message")) {
                            val message = json.getString("message")
                            if (message.isNotEmpty() && message != "Failed") {
                                android.util.Log.d("CheckoutScreen", "✅ Extracted message: $message")
                                return message
                            }
                        }
                        
                        // === Priority 3: Check validation errors ===
                        if (json.has("errors") && !json.isNull("errors")) {
                            try {
                                val errors = json.getJSONObject("errors")
                                val errorsList = mutableListOf<String>()
                                
                                val keys = errors.keys()
                                while (keys.hasNext()) {
                                    val key = keys.next()
                                    val value = errors.get(key)
                                    
                                    val errorText = when (value) {
                                        is String -> "• $key: $value"
                                        else -> "• $key: ${value.toString()}"
                                    }
                                    errorsList.add(errorText)
                                }
                                
                                if (errorsList.isNotEmpty()) {
                                    val validationMsg = "Validasi gagal:\n${errorsList.joinToString("\n")}"
                                    android.util.Log.d("CheckoutScreen", "✅ Extracted validation errors: $validationMsg")
                                    return validationMsg
                                }
                            } catch (e: Exception) {
                                // Ignore nested error parsing issues
                            }
                        }
                        
                    } catch (e: JSONException) {
                        // JSON parsing failed, fall through to string matching
                    }
                }
            }
        }
        
        // === If not JSON or parsing failed, use string matching ===
        when {
            // Authentication errors
            errorMsg.contains("401", ignoreCase = true) || 
            errorMsg.contains("unauthenticated", ignoreCase = true) ||
            errorMsg.contains("sesi", ignoreCase = true) -> 
                "Sesi login Anda telah habis. Silakan login kembali"
            
            // Stock/Availability errors
            errorMsg.contains("stock", ignoreCase = true) || 
            errorMsg.contains("insufficient", ignoreCase = true) ||
            errorMsg.contains("tidak cukup", ignoreCase = true) ->
                "Stok produk tidak mencukupi. Silakan kurangi jumlah pembelian"
            
            errorMsg.contains("not available", ignoreCase = true) ||
            errorMsg.contains("tidak tersedia", ignoreCase = true) ->
                "Produk tidak tersedia untuk saat ini"
            
            // Validation errors (422)
            errorMsg.contains("422", ignoreCase = true) ||
            errorMsg.contains("validation", ignoreCase = true) ||
            errorMsg.contains("validasi", ignoreCase = true) -> {
                // Check if it's a product validation error
                if (errorMsg.contains("produk", ignoreCase = true) || 
                    errorMsg.contains("tidak valid", ignoreCase = true) || 
                    errorMsg.contains("ID produk", ignoreCase = true)) {
                    "Produk yang dipilih tidak valid atau telah dihapus.\n\nSolusi:\n1. Keluar dari aplikasi\n2. Buka Settings > Apps > Trifhop\n3. Tap 'Storage' > 'Clear Cache'\n4. Buka ulang aplikasi\n5. Coba lagi"
                } else {
                    val details = extractErrorDetails(errorMsg)
                    "Data tidak lengkap atau tidak valid.\n$details"
                }
            }
            
            // Server errors
            errorMsg.contains("500", ignoreCase = true) ||
            errorMsg.contains("502", ignoreCase = true) ||
            errorMsg.contains("503", ignoreCase = true) ||
            errorMsg.contains("server error", ignoreCase = true) ->
                "Terjadi kesalahan di server. Silakan coba lagi nanti"
            
            // Network errors
            errorMsg.contains("network", ignoreCase = true) ||
            errorMsg.contains("unable to resolve", ignoreCase = true) ||
            errorMsg.contains("timeout", ignoreCase = true) ||
            errorMsg.contains("ioexception", ignoreCase = true) ||
            errorMsg.contains("tidak dapat terhubung", ignoreCase = true) ->
                "Tidak dapat terhubung ke server. Periksa koneksi internet Anda"
            
            // Payment errors
            errorMsg.contains("payment", ignoreCase = true) ||
            errorMsg.contains("payment", ignoreCase = true) ||
            errorMsg.contains("pembayaran", ignoreCase = true) ->
                "Gagal membuat invoice pembayaran. Silakan coba lagi"
            
            // Default: return detailed error
            else -> extractErrorDetails(errorMsg)
        }
        
    } catch (e: Exception) {
        // === Critical error in parsing ===
        android.util.Log.e("CheckoutScreen", "Error parsing message: ${e.message}", e)
        when {
            errorMsg.contains("401", ignoreCase = true) -> "Sesi login Anda telah habis. Silakan login kembali"
            errorMsg.contains("stock", ignoreCase = true) -> "Stok produk tidak mencukupi"
            errorMsg.contains("network", ignoreCase = true) -> "Tidak dapat terhubung ke server"
            else -> "Terjadi kesalahan. Silakan coba lagi"
        }
    }
}
