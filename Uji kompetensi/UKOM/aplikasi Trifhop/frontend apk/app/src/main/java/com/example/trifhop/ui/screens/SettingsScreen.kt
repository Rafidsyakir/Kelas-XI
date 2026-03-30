package com.example.trifhop.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trifhop.data.local.UserPreferences
import com.example.trifhop.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    userPreferences: UserPreferences
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(false) }
    var promoNotifications by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pengaturan",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Section
            item {
                SectionHeader(title = "Akun")
            }
            
            item {
                SettingsCard {
                    Column {
                        SettingsMenuItem(
                            icon = Icons.Default.Person,
                            title = "Edit Profile",
                            subtitle = "Ubah nama, foto, dan informasi lainnya",
                            onClick = { /* Navigate to edit profile */ }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsMenuItem(
                            icon = Icons.Default.Lock,
                            title = "Ubah Password",
                            subtitle = "Perbarui password akun Anda",
                            onClick = { /* Navigate to change password */ }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsMenuItem(
                            icon = Icons.Default.Email,
                            title = "Verifikasi Email",
                            subtitle = "Verifikasi alamat email Anda",
                            onClick = { /* Navigate to email verification */ }
                        )
                    }
                }
            }
            
            // Notifications Section
            item {
                SectionHeader(title = "Notifikasi")
            }
            
            item {
                SettingsCard {
                    Column {
                        SettingsSwitchItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifikasi Push",
                            subtitle = "Terima notifikasi tentang pesanan",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsSwitchItem(
                            icon = Icons.Default.MailOutline,
                            title = "Email Notifikasi",
                            subtitle = "Terima notifikasi via email",
                            checked = emailNotifications,
                            onCheckedChange = { emailNotifications = it }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsSwitchItem(
                            icon = Icons.Default.Campaign,
                            title = "Promo & Penawaran",
                            subtitle = "Terima info promo terbaru",
                            checked = promoNotifications,
                            onCheckedChange = { promoNotifications = it }
                        )
                    }
                }
            }
            
            // Privacy Section
            item {
                SectionHeader(title = "Privasi")
            }
            
            item {
                SettingsCard {
                    Column {
                        SettingsMenuItem(
                            icon = Icons.Default.Security,
                            title = "Keamanan Akun",
                            subtitle = "Two-factor authentication, login history",
                            onClick = { /* Navigate to security */ }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsMenuItem(
                            icon = Icons.Default.Visibility,
                            title = "Pengaturan Privasi",
                            subtitle = "Kontrol siapa yang dapat melihat profil Anda",
                            onClick = { /* Navigate to privacy settings */ }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsMenuItem(
                            icon = Icons.Default.DataUsage,
                            title = "Data & Storage",
                            subtitle = "Kelola penggunaan data aplikasi",
                            onClick = { /* Navigate to data settings */ }
                        )
                    }
                }
            }
            
            // App Preferences Section
            item {
                SectionHeader(title = "Preferensi Aplikasi")
            }
            
            item {
                SettingsCard {
                    Column {
                        SettingsMenuItem(
                            icon = Icons.Default.Language,
                            title = "Bahasa",
                            subtitle = "Indonesia",
                            onClick = { /* Navigate to language selection */ }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsMenuItem(
                            icon = Icons.Default.DarkMode,
                            title = "Tema Aplikasi",
                            subtitle = "Light Mode",
                            onClick = { /* Navigate to theme selection */ }
                        )
                    }
                }
            }
            
            // About Section
            item {
                SectionHeader(title = "Tentang")
            }
            
            item {
                SettingsCard {
                    Column {
                        SettingsMenuItem(
                            icon = Icons.Default.Info,
                            title = "Tentang Trifhop",
                            subtitle = "Versi 1.0.0",
                            onClick = { /* Show about dialog */ }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsMenuItem(
                            icon = Icons.Default.Description,
                            title = "Syarat & Ketentuan",
                            subtitle = "Baca syarat & ketentuan penggunaan",
                            onClick = { /* Navigate to terms */ }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsMenuItem(
                            icon = Icons.Default.PrivacyTip,
                            title = "Kebijakan Privasi",
                            subtitle = "Baca kebijakan privasi kami",
                            onClick = { /* Navigate to privacy policy */ }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsMenuItem(
                            icon = Icons.Default.Help,
                            title = "Bantuan",
                            subtitle = "FAQ dan pusat bantuan",
                            onClick = { /* Navigate to help */ }
                        )
                    }
                }
            }
            
            // Danger Zone Section
            item {
                SectionHeader(title = "Zona Bahaya")
            }
            
            item {
                SettingsCard {
                    Column {
                        SettingsMenuItem(
                            icon = Icons.Default.Logout,
                            iconTint = Color(0xFFEF4444),
                            title = "Logout",
                            titleColor = Color(0xFFEF4444),
                            subtitle = "Keluar dari akun Anda",
                            onClick = { showLogoutDialog = true }
                        )
                        
                        HorizontalDivider(color = Color(0xFF2D3748))
                        
                        SettingsMenuItem(
                            icon = Icons.Default.DeleteForever,
                            iconTint = Color(0xFFDC2626),
                            title = "Hapus Akun",
                            titleColor = Color(0xFFDC2626),
                            subtitle = "Hapus akun secara permanen",
                            onClick = { showDeleteDialog = true }
                        )
                    }
                }
            }
            
            // Footer Version Info
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trifhop",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF137FEC)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0.0",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "© 2024 Trifhop. All rights reserved.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Apakah Anda yakin ingin keluar dari akun?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            userPreferences.clearUser()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = Color(0xFF94A3B8))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
    
    // Delete Account Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = "Hapus Akun?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            },
            text = {
                Text(
                    "Tindakan ini tidak dapat dibatalkan. Semua data Anda akan dihapus secara permanen.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Implement account deletion
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626)
                    )
                ) {
                    Text("Hapus Akun")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = Color(0xFF94A3B8))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SettingsCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF94A3B8),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

@Composable
fun SettingsMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = Color(0xFF137FEC),
    title: String,
    titleColor: Color = Color.White,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color(0xFF1A1A2E)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1A1A2E)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFF137FEC).copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF137FEC),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF1A1A2E),
                    checkedTrackColor = Color(0xFF137FEC),
                    uncheckedThumbColor = Color(0xFF1A1A2E),
                    uncheckedTrackColor = Color(0xFFE2E8F0)
                )
            )
        }
    }
}
