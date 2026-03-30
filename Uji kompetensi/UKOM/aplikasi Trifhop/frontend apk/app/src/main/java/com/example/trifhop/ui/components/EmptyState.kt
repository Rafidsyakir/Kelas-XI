package com.example.trifhop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Empty State Component - Premium & Engaging
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF137FEC).copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE2E8F0),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Message
        Text(
            text = message,
            fontSize = 15.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        
        // Optional Action Button
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onActionClick,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF137FEC),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = actionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Pre-configured Empty States for common scenarios
 */
@Composable
fun EmptyCartState(onBrowseClick: () -> Unit) {
    EmptyState(
        icon = Icons.Outlined.ShoppingCart,
        title = "Keranjang Masih Kosong",
        message = "Belum ada produk yang kamu pilih.\nYuk, mulai belanja sekarang!",
        actionText = "Jelajahi Produk",
        onActionClick = onBrowseClick
    )
}

@Composable
fun EmptyWishlistState(onBrowseClick: () -> Unit) {
    EmptyState(
        icon = Icons.Outlined.FavoriteBorder,
        title = "Wishlist Masih Kosong",
        message = "Simpan produk favoritmu di sini\nagar mudah ditemukan nanti!",
        actionText = "Cari Produk Favorit",
        onActionClick = onBrowseClick
    )
}

@Composable
fun EmptyOrderHistoryState() {
    EmptyState(
        icon = Icons.Outlined.Receipt,
        title = "Belum Ada Pesanan",
        message = "Riwayat pesanan kamu akan\nmuncul di sini setelah checkout.",
        actionText = null,
        onActionClick = null
    )
}

@Composable
fun EmptyNotificationsState() {
    EmptyState(
        icon = Icons.Outlined.Notifications,
        title = "Tidak Ada Notifikasi",
        message = "Kamu akan mendapat notifikasi tentang\npesanan dan promo spesial di sini.",
        actionText = null,
        onActionClick = null
    )
}

@Composable
fun EmptySearchResultState() {
    EmptyState(
        icon = Icons.Outlined.SearchOff,
        title = "Produk Tidak Ditemukan",
        message = "Coba gunakan kata kunci lain\natau jelajahi kategori yang tersedia.",
        actionText = null,
        onActionClick = null
    )
}

@Composable
fun ErrorState(
    message: String,
    onRetry: (() -> Unit)? = null
) {
    EmptyState(
        icon = Icons.Outlined.ErrorOutline,
        title = "Oops, Ada Masalah",
        message = message,
        actionText = if (onRetry != null) "Coba Lagi" else null,
        onActionClick = onRetry
    )
}
