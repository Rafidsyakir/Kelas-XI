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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasimonitoringkelas3.data.api.RetrofitClient
import com.example.aplikasimonitoringkelas3.data.model.*
import com.example.aplikasimonitoringkelas3.data.repository.SiswaRepository
import com.example.aplikasimonitoringkelas3.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SiswaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplikasiMonitoringKelas3Theme(darkTheme = true) {
                SiswaScreen(
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
fun SiswaScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val tabs = listOf(
        TabItem("Home", Icons.Default.Home),
        TabItem("Schedule", Icons.Default.DateRange),
        TabItem("Attendance", Icons.Default.Person),
        TabItem("Profile", Icons.Default.AccountCircle)
    )
    
    Scaffold(
        containerColor = SpaceBlack,
        bottomBar = {
            NavigationBar(
                containerColor = CardDark,
                contentColor = GradientCyan,
                tonalElevation = 0.dp,
                modifier = Modifier.border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(listOf(GlassBorder, Color.Transparent)),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 11.sp) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GradientCyan,
                            selectedTextColor = GradientCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = GradientBlue.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceBlack)
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
                },
                label = "tab_content"
            ) { tab ->
                when (tab) {
                    0 -> HomePage()
                    1 -> JadwalPage()
                    2 -> KehadiranPage()
                    3 -> ProfilePage(onLogout)
                }
            }
        }
    }
}

data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun HomePage() {
    val repository = remember { SiswaRepository() }
    val scope = rememberCoroutineScope()
    
    var jadwalHariIni by remember { mutableStateOf<List<Jadwal>>(emptyList()) }
    var kelasInfo by remember { mutableStateOf<Kelas?>(null) }
    var hariIni by remember { mutableStateOf("") }
    var tanggalIni by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = repository.getJadwalHariIni()
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    jadwalHariIni = data?.jadwal ?: emptyList()
                    kelasInfo = data?.kelas
                    hariIni = data?.hari ?: ""
                    tanggalIni = data?.tanggal ?: ""
                } else {
                    error = response.body()?.message ?: "Failed to load schedule"
                }
            } catch (e: Exception) {
                error = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF0D9488),
                            Color(0xFFF97316)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Welcome! 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                kelasInfo?.let {
                    Text(
                        text = "Class: ${it.namaKelas}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hariIni.isNotEmpty()) "$hariIni, $tanggalIni" else "Loading...",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "📚 Today's Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error ?: "Error",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else if (jadwalHariIni.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No schedule for today",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Time to relax! 🎉",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(jadwalHariIni) { index, jadwal ->
                    JadwalCard(jadwal = jadwal, index = index)
                }
            }
        }
    }
}

@Composable
fun JadwalCard(jadwal: Jadwal, index: Int) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(index) {
        delay(index * 100L)
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
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = jadwal.jamMulai,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "to",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = jadwal.jamSelesai,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Info Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = jadwal.mataPelajaran,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = jadwal.guru?.nama ?: "Teacher",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    jadwal.ruangan?.let { ruangan ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = ruangan,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPage() {
    val repository = remember { SiswaRepository() }
    val scope = rememberCoroutineScope()
    
    var jadwalPerHari by remember { mutableStateOf<Map<String, List<Jadwal>>>(emptyMap()) }
    var kelasInfo by remember { mutableStateOf<Kelas?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedDay by remember { mutableStateOf("Monday") }
    
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = repository.getJadwalByKelas()
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    jadwalPerHari = data?.jadwal ?: emptyMap()
                    kelasInfo = data?.kelas
                } else {
                    error = response.body()?.message ?: "Failed to load schedule"
                }
            } catch (e: Exception) {
                error = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "📅 Class Schedule",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                kelasInfo?.let {
                    Text(
                        text = it.namaKelas,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
        
        // Day Tabs
        ScrollableTabRow(
            selectedTabIndex = days.indexOf(selectedDay),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 8.dp
        ) {
            days.forEach { day ->
                Tab(
                    selected = selectedDay == day,
                    onClick = { selectedDay = day },
                    text = { 
                        Text(
                            day,
                            fontWeight = if (selectedDay == day) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        
        // Content
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = error ?: "Error",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        } else {
            val jadwalHari = jadwalPerHari[selectedDay] ?: emptyList()
            
            if (jadwalHari.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No schedule on $selectedDay",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(jadwalHari) { index, jadwal ->
                        JadwalCard(jadwal = jadwal, index = index)
                    }
                }
            }
        }
    }
}

@Composable
fun KehadiranPage() {
    val repository = remember { SiswaRepository() }
    val scope = rememberCoroutineScope()
    
    var guruPenggantiList by remember { mutableStateOf<List<GuruPenggantiItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var selectedItem by remember { mutableStateOf<GuruPenggantiItem?>(null) }
    var showAttendanceDialog by remember { mutableStateOf(false) }
    var attendanceStatus by remember { mutableStateOf("Hadir") }
    var attendanceNote by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = repository.getGuruPengganti()
            if (response.isSuccessful && response.body()?.success == true) {
                guruPenggantiList = response.body()?.data ?: emptyList()
                errorMessage = null
            } else {
                errorMessage = response.body()?.message ?: "Failed to load data"
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error"
        } finally {
            isLoading = false
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
                        colors = listOf(
                            Color(0xFF6366F1),
                            Color(0xFF8B5CF6)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "👥 Teacher Substitutes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFe2e8f0)
                )
                Text(
                    text = "View substitute teacher assignments",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF94a3b8)
                )
            }
        }
        
        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6366F1))
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Error",
                        color = Color(0xFFef4444),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            guruPenggantiList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📋",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Substitute Teachers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFe2e8f0)
                        )
                        Text(
                            text = "There are no substitute teacher assignments for your class",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94a3b8),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(guruPenggantiList) { item ->
                        GuruPenggantiClickableCard(
                            item = item,
                            onClick = {
                                selectedItem = item
                                attendanceStatus = "Hadir"
                                attendanceNote = ""
                                showAttendanceDialog = true
                            }
                        )
                    }
                }
            }
        }
        
        // Snackbar for messages
        snackbarMessage?.let { message ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1e293b),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message,
                            color = Color(0xFFe2e8f0),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("OK", color = Color(0xFF06b6d4))
                        }
                    }
                }
            }
        }
    }
    
    // Attendance Input Dialog
    if (showAttendanceDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showAttendanceDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Kehadiran Guru Pengganti",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFe2e8f0)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Guru Pengganti: ${selectedItem?.guruPengganti?.nama ?: "N/A"}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFe2e8f0)
                    )
                    Text(
                        text = "Menggantikan: ${selectedItem?.guruAsli?.nama}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94a3b8)
                    )
                    Text(
                        text = "Mata Pelajaran: ${selectedItem?.jadwal?.mataPelajaran}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94a3b8)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Status Kehadiran:",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFe2e8f0)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Hadir", "Terlambat", "Tidak Hadir", "Izin").forEach { status ->
                            FilterChip(
                                selected = attendanceStatus == status,
                                onClick = { attendanceStatus = status },
                                label = { Text(status, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (status) {
                                        "Hadir" -> Color(0xFF22C55E).copy(alpha = 0.3f)
                                        "Terlambat" -> Color(0xFFF59E0B).copy(alpha = 0.3f)
                                        "Tidak Hadir" -> Color(0xFFEF4444).copy(alpha = 0.3f)
                                        "Izin" -> Color(0xFF3B82F6).copy(alpha = 0.3f)
                                        else -> Color(0xFF334155)
                                    },
                                    labelColor = Color(0xFFe2e8f0),
                                    selectedLabelColor = Color(0xFFe2e8f0)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = attendanceNote,
                        onValueChange = { attendanceNote = it },
                        label = { Text("Catatan (opsional)", color = Color(0xFF94a3b8)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFFe2e8f0),
                            unfocusedTextColor = Color(0xFFe2e8f0),
                            focusedBorderColor = Color(0xFF06b6d4),
                            unfocusedBorderColor = Color(0xFF334155)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            isSubmitting = true
                            try {
                                val request = TeacherAttendanceRequest(
                                    guruId = selectedItem?.guruPengganti?.id ?: 0,
                                    status = attendanceStatus,
                                    keterangan = attendanceNote.ifEmpty { null }
                                )
                                val response = repository.createTeacherAttendance(request)
                                if (response.isSuccessful) {
                                    snackbarMessage = "Kehadiran guru pengganti berhasil dicatat!"
                                    showAttendanceDialog = false
                                } else {
                                    snackbarMessage = "Gagal mencatat kehadiran: ${response.message()}"
                                }
                            } catch (e: Exception) {
                                snackbarMessage = "Error: ${e.message}"
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF06b6d4)
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Simpan")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAttendanceDialog = false }
                ) {
                    Text("Batal", color = Color(0xFF94a3b8))
                }
            },
            containerColor = Color(0xFF1e293b)
        )
    }
}

@Composable
fun GuruPenggantiClickableCard(
    item: GuruPenggantiItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1e293b))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.jadwal.mataPelajaran,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF06b6d4)
                    )
                    Text(
                        text = "${item.jadwal.jamMulai} - ${item.jadwal.jamSelesai}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94a3b8)
                    )
                }
                
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (item.status) {
                        "pending" -> Color(0xFFFBBF24).copy(alpha = 0.2f)
                        "approved" -> Color(0xFF10B981).copy(alpha = 0.2f)
                        "rejected" -> Color(0xFFEF4444).copy(alpha = 0.2f)
                        else -> Color(0xFF6B7280).copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = when (item.status) {
                            "pending" -> "⏳ Pending"
                            "approved" -> "✅ Approved"
                            "rejected" -> "❌ Rejected"
                            else -> item.status
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = when (item.status) {
                            "pending" -> Color(0xFFFBBF24)
                            "approved" -> Color(0xFF10B981)
                            "rejected" -> Color(0xFFEF4444)
                            else -> Color(0xFF6B7280)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(16.dp))
            
            // Original Teacher -> Substitute Teacher
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Original Teacher
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Original Teacher",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94a3b8)
                    )
                    Text(
                        text = item.guruAsli.nama,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFe2e8f0)
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF06b6d4),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                // Substitute Teacher
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Substitute",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94a3b8)
                    )
                    Text(
                        text = item.guruPengganti?.nama ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFe2e8f0)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Date & Class Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF94a3b8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.tanggal,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94a3b8)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color(0xFF94a3b8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.jadwal.kelas?.namaKelas ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94a3b8)
                    )
                }
            }
            
            // Reason (if present)
            if (item.alasan.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF334155)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF06b6d4),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Reason",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94a3b8)
                            )
                            Text(
                                text = item.alasan,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFe2e8f0)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    count: Int,
    color: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun GuruAttendanceCard(
    guruStatus: GuruAttendanceStatus,
    statusColors: Map<String, Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = guruStatus.guru.nama.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Info
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Status Badge
            if (guruStatus.hasAttendance && guruStatus.attendance != null) {
                val status = guruStatus.attendance.status
                val color = statusColors[status] ?: MaterialTheme.colorScheme.primary
                
                Surface(
                    color = color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = color,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Surface(
                    color = Color.Gray.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Not Recorded",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ProfilePage(onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Student",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = "Class Monitoring App",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        
        // Menu Items
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "About App",
                onClick = { }
            )
            
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = { }
            )
            
            ProfileMenuItem(
                icon = Icons.Default.ExitToApp,
                title = "Logout",
                onClick = { showLogoutDialog = true },
                textColor = MaterialTheme.colorScheme.error
            )
        }
    }
    
    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout from the app?") },
            confirmButton = {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
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
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = textColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== SISWA GURU PENGGANTI PAGE ====================
@Composable
fun SiswaGuruPenggantiPage() {
    val repository = remember { SiswaRepository() }
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
                        colors = listOf(GradientBlue, GradientCyan)
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
                    "Informasi penggantian guru hari ini",
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
                CircularProgressIndicator(color = GradientCyan)
            }
        } else if (errorMessage != null) {
            SiswaEmptyState(errorMessage!!)
        } else if (guruPenggantiList.isEmpty()) {
            SiswaEmptyState("Tidak ada guru pengganti hari ini")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(guruPenggantiList) { item ->
                    SiswaGuruPenggantiCard(item)
                }
            }
        }
    }
}

@Composable
fun SiswaGuruPenggantiCard(item: GuruPenggantiItem) {
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
            // Header dengan mata pelajaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.jadwal.mataPelajaran,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GradientCyan
                    )
                    Text(
                        "${item.jadwal.jamMulai} - ${item.jadwal.jamSelesai}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94a3b8)
                    )
                }
                
                SiswaStatusBadge(item.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Guru Asli -> Guru Pengganti
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Guru Asli
                Column(modifier = Modifier.weight(1f)) {
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
                    tint = AuroraGold,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = AuroraGold,
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
        }
    }
}

@Composable
fun SiswaStatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "Pending" -> AuroraGold.copy(alpha = 0.15f) to AuroraGold
        "Disetujui" -> NeonSuccess.copy(alpha = 0.15f) to NeonSuccess
        "Ditolak" -> NeonError.copy(alpha = 0.15f) to NeonError
        "Selesai" -> GradientCyan.copy(alpha = 0.15f) to GradientCyan
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

@Composable
fun SiswaEmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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
