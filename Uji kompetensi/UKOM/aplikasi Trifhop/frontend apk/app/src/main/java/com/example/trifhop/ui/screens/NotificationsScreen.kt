package com.example.trifhop.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pesanan", "Promo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifikasi",
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
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1A1A2E),
                contentColor = Color(0xFF137FEC),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF137FEC),
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selectedContentColor = Color(0xFF137FEC),
                        unselectedContentColor = Color.Gray
                    )
                }
            }
            
            // Content based on selected tab
            when (selectedTab) {
                0 -> OrderNotificationsContent()
                1 -> PromoNotificationsContent()
            }
        }
    }
}

@Composable
fun OrderNotificationsContent() {
    // Sample order notifications
    val notifications = remember {
        listOf(
            NotificationItem(
                id = 1,
                title = "Pesanan Sedang Dikemas",
                message = "Pesanan #INV001 sedang dikemas oleh penjual. Estimasi pengiriman 1-2 hari.",
                time = "2 jam lalu",
                icon = Icons.Default.Inventory,
                iconBg = Color(0xFFDCEFFF),
                iconTint = Color(0xFF2563EB),
                isRead = false
            ),
            NotificationItem(
                id = 2,
                title = "Pesanan Telah Dikirim",
                message = "Pesanan #INV002 telah dikirim. Lacak pesanan untuk info lebih lanjut.",
                time = "1 hari lalu",
                icon = Icons.Default.LocalShipping,
                iconBg = Color(0xFFDBEAFE),
                iconTint = Color(0xFF1D4ED8),
                isRead = false
            ),
            NotificationItem(
                id = 3,
                title = "Pesanan Selesai",
                message = "Pesanan #INV003 telah sampai dan selesai. Terima kasih sudah berbelanja!",
                time = "3 hari lalu",
                icon = Icons.Default.CheckCircle,
                iconBg = Color(0xFFD1FAE5),
                iconTint = Color(0xFF059669),
                isRead = true
            ),
            NotificationItem(
                id = 4,
                title = "Menunggu Pembayaran",
                message = "Pesanan #INV004 menunggu pembayaran. Segera bayar sebelum 23:59 hari ini.",
                time = "5 hari lalu",
                icon = Icons.Default.Schedule,
                iconBg = Color(0xFFFEF3C7),
                iconTint = Color(0xFFD97706),
                isRead = true
            )
        )
    }
    
    if (notifications.isEmpty()) {
        EmptyNotificationState(
            icon = Icons.Default.Notifications,
            title = "Belum Ada Notifikasi",
            message = "Notifikasi pesanan akan muncul di sini"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(notifications) { notification ->
                NotificationCard(notification = notification)
            }
        }
    }
}

@Composable
fun PromoNotificationsContent() {
    // Sample promo notifications
    val notifications = remember {
        listOf(
            NotificationItem(
                id = 5,
                title = "Flash Sale 50% OFF!",
                message = "Dapatkan diskon hingga 50% untuk semua kategori. Hanya hari ini!",
                time = "30 menit lalu",
                icon = Icons.Default.LocalOffer,
                iconBg = Color(0xFFFEE2E2),
                iconTint = Color(0xFFEF4444),
                isRead = false
            ),
            NotificationItem(
                id = 6,
                title = "Koleksi Baru Tiba",
                message = "Lihat koleksi thrift terbaru kami dengan harga mulai dari Rp 25.000!",
                time = "2 jam lalu",
                icon = Icons.Default.NewReleases,
                iconBg = Color(0xFFE9D5FF),
                iconTint = Color(0xFF9333EA),
                isRead = false
            ),
            NotificationItem(
                id = 7,
                title = "Gratis Ongkir Minimum Rp 100.000",
                message = "Belanja minimal Rp 100.000 dan dapatkan gratis ongkir ke seluruh Indonesia.",
                time = "1 hari lalu",
                icon = Icons.Default.CardGiftcard,
                iconBg = Color(0xFFFED7AA),
                iconTint = Color(0xFFEA580C),
                isRead = true
            ),
            NotificationItem(
                id = 8,
                title = "Voucher Khusus Untukmu",
                message = "Selamat! Kamu mendapat voucher diskon 20% untuk pembelian selanjutnya.",
                time = "3 hari lalu",
                icon = Icons.Default.ConfirmationNumber,
                iconBg = Color(0xFFFEF3C7),
                iconTint = Color(0xFFD97706),
                isRead = true
            )
        )
    }
    
    if (notifications.isEmpty()) {
        EmptyNotificationState(
            icon = Icons.Default.Campaign,
            title = "Belum Ada Promo",
            message = "Promo dan penawaran menarik akan muncul di sini"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(notifications) { notification ->
                NotificationCard(notification = notification)
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ },
        color = if (notification.isRead) Color(0xFF1A1A2E) else Color(0xFF1E2D3D)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = notification.iconBg
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = notification.icon,
                        contentDescription = null,
                        tint = notification.iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 14.sp,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = Color(0xFF137FEC),
                                    shape = CircleShape
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = notification.time,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
        
        // Bottom Divider
        HorizontalDivider(color = Color(0xFF2D3748), modifier = Modifier.padding(start = 76.dp)
        )
    }
}

@Composable
fun EmptyNotificationState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color(0xFF94A3B8),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val isRead: Boolean
)
