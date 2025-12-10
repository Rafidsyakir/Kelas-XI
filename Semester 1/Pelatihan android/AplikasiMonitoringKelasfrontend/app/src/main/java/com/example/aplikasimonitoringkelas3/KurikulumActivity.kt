package com.example.aplikasimonitoringkelas3

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasimonitoringkelas3.data.api.RetrofitClient
import com.example.aplikasimonitoringkelas3.data.model.*
import com.example.aplikasimonitoringkelas3.data.repository.KurikulumRepository
import com.example.aplikasimonitoringkelas3.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Aurora Color Palette for Kurikulum
val PrimaryBlue = GradientBlue
val SecondaryPurple = AuroraPurple
val AccentPink = AuroraPink
val SuccessGreen = NeonSuccess
val WarningOrange = AuroraGold
val DangerRed = NeonError
val InfoCyan = GradientCyan

class KurikulumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplikasiMonitoringKelas3Theme(darkTheme = true) {
                KurikulumScreen(
                    onLogout = {
                        RetrofitClient.clearToken()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val tabs = listOf(
        KurikulumTab("Dashboard", Icons.Default.Home),
        KurikulumTab("Attendance", Icons.Default.CheckCircle),
        KurikulumTab("Schedule", Icons.Default.DateRange),
        KurikulumTab("Teachers", Icons.Default.Person),
        KurikulumTab("Substitutes", Icons.Default.Refresh),
        KurikulumTab("Profile", Icons.Default.AccountCircle)
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = PrimaryBlue,
                tonalElevation = 12.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                tab.icon, 
                                contentDescription = tab.title,
                                modifier = Modifier.size(if (selectedTab == index) 28.dp else 24.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                tab.title, 
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = PrimaryBlue.copy(alpha = 0.1f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                        initialOffsetX = { if (targetState > initialState) it else -it }
                    ) togetherWith fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                        targetOffsetX = { if (targetState > initialState) -it else it }
                    )
                },
                label = "tab_content"
            ) { tab ->
                when (tab) {
                    0 -> KurikulumDashboardPage()
                    1 -> KurikulumKehadiranPage()
                    2 -> KurikulumJadwalPage()
                    3 -> KurikulumGuruPage()
                    4 -> KurikulumGuruPenggantiPage()
                    5 -> KurikulumProfilePage(onLogout)
                }
            }
        }
    }
}

data class KurikulumTab(val title: String, val icon: ImageVector)

// ==================== DASHBOARD PAGE ====================
@Composable
fun KurikulumDashboardPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var dashboardData by remember { mutableStateOf<KurikulumDashboardData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = repository.getDashboard()
                if (response.isSuccessful && response.body()?.success == true) {
                    dashboardData = response.body()?.data
                } else {
                    error = response.body()?.message ?: "Failed to load dashboard"
                }
            } catch (e: Exception) {
                error = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Gradient
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryBlue, SecondaryPurple)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Welcome! 👋",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Curriculum",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    dashboardData?.let { data ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${data.hari}, ${data.tanggalFormatted}",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
        } else if (error != null) {
            item {
                ErrorCard(error ?: "Error")
            }
        } else {
            dashboardData?.let { data ->
                // Summary Cards Row
                item {
                    Text(
                        text = "📊 Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardSummaryCard(
                            title = "Total Teachers",
                            value = data.summary.totalGuru.toString(),
                            icon = Icons.Default.Person,
                            color = PrimaryBlue,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardSummaryCard(
                            title = "Total Schedules",
                            value = data.summary.totalJadwal.toString(),
                            icon = Icons.Default.DateRange,
                            color = SecondaryPurple,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardSummaryCard(
                            title = "Total Classes",
                            value = data.summary.totalKelas.toString(),
                            icon = Icons.Default.Home,
                            color = AccentPink,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Kehadiran Hari Ini
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ Today's Attendance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    KehadiranStatsCard(data.kehadiranHariIni)
                }
                
                // Jadwal Hari Ini
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📚 Today's Schedule (${data.jadwalHariIni.size} Schedules)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (data.jadwalHariIni.isEmpty()) {
                    item {
                        EmptyStateCard("No schedule for today")
                    }
                } else {
                    itemsIndexed(data.jadwalHariIni.take(5)) { index, jadwal ->
                        AnimatedJadwalCard(jadwal = jadwal, index = index)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardSummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun KehadiranStatsCard(kehadiran: KehadiranSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                KehadiranStatItem("Present", kehadiran.hadir, SuccessGreen)
                KehadiranStatItem("Late", kehadiran.terlambat, WarningOrange)
                KehadiranStatItem("Absent", kehadiran.tidakHadir, DangerRed)
                KehadiranStatItem("Excused", kehadiran.izin, InfoCyan)
            }
            
            if (kehadiran.belumInput > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.1f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${kehadiran.belumInput} guru belum input kehadiran",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun KehadiranStatItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            maxLines = 1
        )
    }
}

@Composable
fun AnimatedJadwalCard(jadwal: Jadwal, index: Int) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(index) {
        delay(index * 80L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time Badge
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryBlue.copy(alpha = 0.1f))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = jadwal.jamMulai,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        text = jadwal.jamSelesai,
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = jadwal.mataPelajaran,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = jadwal.guru?.nama ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = jadwal.kelas?.namaKelas ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// ==================== KEHADIRAN PAGE ====================
@Composable
fun KurikulumKehadiranPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var laporanData by remember { mutableStateOf<LaporanHarianData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = repository.getLaporanHarian()
                if (response.isSuccessful && response.body()?.success == true) {
                    laporanData = response.body()?.data
                } else {
                    error = response.body()?.message ?: "Gagal memuat data"
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(SuccessGreen, InfoCyan)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "✅ Laporan Kehadiran",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                laporanData?.let {
                    Text(
                        text = it.tanggalFormatted,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SuccessGreen)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ErrorCard(error ?: "Error")
            }
        } else {
            laporanData?.let { data ->
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary Card
                    item {
                        KehadiranStatsCard(data.summary)
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📋 Detail Kehadiran Guru",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    itemsIndexed(data.detail) { index, guruStatus ->
                        AnimatedGuruStatusCard(guruStatus, index)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedGuruStatusCard(guruStatus: GuruStatusHarian, index: Int) {
    var visible by remember { mutableStateOf(false) }
    
    val statusColor = when (guruStatus.status) {
        "Hadir" -> SuccessGreen
        "Terlambat" -> WarningOrange
        "Tidak Hadir" -> DangerRed
        "Izin" -> InfoCyan
        else -> Color.Gray
    }
    
    LaunchedEffect(index) {
        delay(index * 50L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = guruStatus.guru.nama.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = guruStatus.guru.nama,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    guruStatus.guru.mataPelajaran?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    guruStatus.jamMasuk?.let {
                        Text(
                            text = "Masuk: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                // Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = guruStatus.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ==================== JADWAL PAGE ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumJadwalPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var jadwalList by remember { mutableStateOf<List<Jadwal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedHari by remember { mutableStateOf<String?>(null) }
    
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    
    fun loadJadwal() {
        scope.launch {
            isLoading = true
            try {
                val response = repository.getJadwal(hari = selectedHari)
                if (response.isSuccessful && response.body()?.success == true) {
                    jadwalList = response.body()?.data ?: emptyList()
                    error = null
                } else {
                    error = response.body()?.message
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(selectedHari) {
        loadJadwal()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(SecondaryPurple, AccentPink)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "📅 Jadwal Pelajaran",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Kelola jadwal semua kelas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        
        // Filter Hari
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedHari == null,
                    onClick = { selectedHari = null },
                    label = { Text("Semua") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SecondaryPurple,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(hariList) { hari ->
                FilterChip(
                    selected = selectedHari == hari,
                    onClick = { selectedHari = hari },
                    label = { Text(hari) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SecondaryPurple,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SecondaryPurple)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ErrorCard(error ?: "Error")
            }
        } else if (jadwalList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard("Tidak ada jadwal")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(jadwalList) { index, jadwal ->
                    AnimatedJadwalDetailCard(jadwal, index)
                }
            }
        }
    }
}

@Composable
fun AnimatedJadwalDetailCard(jadwal: Jadwal, index: Int) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(index) {
        delay(index * 50L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.9f)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hari Badge
                    Surface(
                        color = SecondaryPurple.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = jadwal.hari,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = SecondaryPurple,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Time
                    Text(
                        text = "${jadwal.jamMulai} - ${jadwal.jamSelesai}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = jadwal.mataPelajaran,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = jadwal.guru?.nama ?: "-",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = jadwal.kelas?.namaKelas ?: "-",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// ==================== GURU PAGE ====================
@Composable
fun KurikulumGuruPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var guruList by remember { mutableStateOf<List<GuruWithStats>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = repository.getListGuru()
                if (response.isSuccessful && response.body()?.success == true) {
                    guruList = response.body()?.data ?: emptyList()
                } else {
                    error = response.body()?.message
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryBlue, InfoCyan)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "👨‍🏫 Daftar Guru",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${guruList.size} guru terdaftar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ErrorCard(error ?: "Error")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(guruList) { index, guruWithStats ->
                    AnimatedGuruCard(guruWithStats, index)
                }
            }
        }
    }
}

@Composable
fun AnimatedGuruCard(guruWithStats: GuruWithStats, index: Int) {
    var visible by remember { mutableStateOf(false) }
    
    val statusColor = when (guruWithStats.statusHariIni) {
        "Hadir" -> SuccessGreen
        "Terlambat" -> WarningOrange
        "Tidak Hadir" -> DangerRed
        "Izin" -> InfoCyan
        else -> Color.Gray
    }
    
    LaunchedEffect(index) {
        delay(index * 60L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryBlue, SecondaryPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = guruWithStats.guru.nama.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = guruWithStats.guru.nama,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        guruWithStats.guru.mataPelajaran?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        guruWithStats.guru.nip?.let {
                            Text(
                                text = "NIP: $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${guruWithStats.statistikBulanIni.persentaseHadir}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Text(
                            text = "Kehadiran",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = guruWithStats.totalJadwal.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryPurple
                        )
                        Text(
                            text = "Jadwal",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            color = statusColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = guruWithStats.statusHariIni,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Hari Ini",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// ==================== PROFILE PAGE ====================
@Composable
fun KurikulumProfilePage(onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryBlue, SecondaryPurple)
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = PrimaryBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Kurikulum",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = "Aplikasi Monitoring Kelas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        
        // Menu Items
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ProfileMenuCard(
                icon = Icons.Default.Info,
                title = "About App",
                subtitle = "Version 1.0.0",
                onClick = { }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileMenuCard(
                icon = Icons.Default.Settings,
                title = "Settings",
                subtitle = "App configuration",
                onClick = { }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileMenuCard(
                icon = Icons.Default.ExitToApp,
                title = "Logout",
                subtitle = "Logout from app",
                onClick = { showLogoutDialog = true },
                iconColor = DangerRed,
                titleColor = DangerRed
            )
        }
    }
    
    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { 
                Text(
                    "Confirm Logout",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { Text("Are you sure you want to logout from the app?") },
            confirmButton = {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconColor: Color = PrimaryBlue,
    titleColor: Color = Color.Black
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

// ==================== COMMON COMPONENTS ====================
@Composable
fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = DangerRed
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = DangerRed,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF475569)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF94a3b8),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== GURU PENGGANTI PAGE ====================
@Composable
fun KurikulumGuruPenggantiPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var guruPenggantiList by remember { mutableStateOf<List<GuruPenggantiItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = repository.getGuruPengganti()
                if (response.isSuccessful && response.body()?.success == true) {
                    guruPenggantiList = response.body()?.data ?: emptyList()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal memuat data"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Terjadi kesalahan"
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0f172a))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(PrimaryBlue, SecondaryPurple)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    "Guru Pengganti",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Kelola penggantian guru hari ini",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (errorMessage != null) {
            EmptyStateCard(errorMessage!!)
        } else if (guruPenggantiList.isEmpty()) {
            EmptyStateCard("Tidak ada guru pengganti hari ini")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(guruPenggantiList) { item ->
                    GuruPenggantiCard(item, repository, scope) {
                        // Refresh list after action
                        scope.launch {
                            try {
                                val response = repository.getGuruPengganti()
                                if (response.isSuccessful && response.body()?.success == true) {
                                    guruPenggantiList = response.body()?.data ?: emptyList()
                                }
                            } catch (e: Exception) {
                                // Handle error
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuruPenggantiCard(
    item: GuruPenggantiItem,
    repository: KurikulumRepository,
    scope: kotlinx.coroutines.CoroutineScope,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1e293b))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header dengan tanggal dan status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.tanggalFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                
                StatusBadge(item.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Jadwal Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = InfoCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        item.jadwal.mataPelajaran,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFe2e8f0)
                    )
                    Text(
                        "${item.jadwal.kelas?.namaKelas ?: "N/A"} • ${item.jadwal.jamMulai}-${item.jadwal.jamSelesai}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94a3b8)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Guru Asli -> Guru Pengganti
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Guru Asli
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Guru Asli",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94a3b8)
                    )
                    Text(
                        item.guruAsli.nama,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFe2e8f0)
                    )
                }
                
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = WarningOrange,
                    modifier = Modifier.size(24.dp)
                )
                
                // Guru Pengganti
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "Guru Pengganti",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94a3b8)
                    )
                    Text(
                        item.guruPengganti?.nama ?: "Belum ditentukan",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End,
                        color = Color(0xFFe2e8f0)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Alasan
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = WarningOrange,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Alasan: ${item.alasan}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94a3b8)
                )
            }
            
            if (item.keterangan != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    item.keterangan,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94a3b8),
                    modifier = Modifier.padding(start = 26.dp)
                )
            }
            
            // Action buttons for pending status
            if (item.status == "Pending") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    repository.updateGuruPenggantiStatus(item.id, "Disetujui")
                                    onRefresh()
                                } catch (e: Exception) {
                                    // Handle error
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Setujui")
                    }
                    
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    repository.updateGuruPenggantiStatus(item.id, "Ditolak")
                                    onRefresh()
                                } catch (e: Exception) {
                                    // Handle error
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerRed
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tolak")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "Pending" -> WarningOrange.copy(alpha = 0.15f) to WarningOrange
        "Disetujui" -> SuccessGreen.copy(alpha = 0.15f) to SuccessGreen
        "Ditolak" -> DangerRed.copy(alpha = 0.15f) to DangerRed
        "Selesai" -> InfoCyan.copy(alpha = 0.15f) to InfoCyan
        else -> Color.Gray.copy(alpha = 0.15f) to Color.Gray
    }
    
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
