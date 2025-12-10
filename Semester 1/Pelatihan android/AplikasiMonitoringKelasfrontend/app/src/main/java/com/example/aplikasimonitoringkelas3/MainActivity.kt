package com.example.aplikasimonitoringkelas3

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import android.util.Patterns
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasimonitoringkelas3.data.repository.UserRepository
import com.example.aplikasimonitoringkelas3.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelas3Theme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SpaceBlack
                ) {
                    AuroraLoginScreen()
                }
            }
        }
    }
}

// ================================
// AURORA BACKGROUND EFFECTS
// ================================

data class Particle(val x: Float, val y: Float, val size: Float, val speed: Float, val alpha: Float)

@Composable
fun FloatingParticles() {
    val particles = remember {
        List(40) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 1f,
                speed = Random.nextFloat() * 0.3f + 0.1f,
                alpha = Random.nextFloat() * 0.4f + 0.1f
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val y = (particle.y + time * particle.speed) % 1f
            drawCircle(
                color = GradientCyan.copy(alpha = particle.alpha),
                radius = particle.size.dp.toPx(),
                center = Offset(particle.x * size.width, y * size.height)
            )
        }
    }
}

@Composable
fun AuroraGlow() {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Restart),
        label = "offset1"
    )
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 180f, targetValue = 540f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label = "offset2"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Purple glow
        drawCircle(
            brush = Brush.radialGradient(listOf(AuroraPurple.copy(alpha = 0.4f), Color.Transparent)),
            radius = 350f,
            center = Offset(
                size.width * 0.2f + cos(Math.toRadians(offset1.toDouble())).toFloat() * 100f,
                size.height * 0.25f + sin(Math.toRadians(offset1.toDouble())).toFloat() * 80f
            )
        )
        // Blue glow
        drawCircle(
            brush = Brush.radialGradient(listOf(GradientBlue.copy(alpha = 0.3f), Color.Transparent)),
            radius = 300f,
            center = Offset(
                size.width * 0.8f + cos(Math.toRadians(offset2.toDouble())).toFloat() * 120f,
                size.height * 0.4f + sin(Math.toRadians(offset2.toDouble())).toFloat() * 60f
            )
        )
        // Cyan accent
        drawCircle(
            brush = Brush.radialGradient(listOf(GradientCyan.copy(alpha = 0.2f), Color.Transparent)),
            radius = 250f,
            center = Offset(
                size.width * 0.5f + sin(Math.toRadians(offset1.toDouble())).toFloat() * 80f,
                size.height * 0.7f + cos(Math.toRadians(offset2.toDouble())).toFloat() * 100f
            )
        )
    }
}

@Composable
fun LoadingRing() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )
    
    Canvas(modifier = Modifier.size(24.dp).rotate(rotation)) {
        drawArc(
            brush = Brush.sweepGradient(listOf(Color.Transparent, GradientCyan, GradientBlue, AuroraPurple)),
            startAngle = 0f, sweepAngle = 280f, useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )
    }
}

// ================================
// GLASSMORPHISM COMPONENTS
// ================================

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextMuted) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GradientCyan,
            unfocusedBorderColor = GlassBorder,
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
            cursorColor = GradientCyan,
            focusedTextColor = TextBright,
            unfocusedTextColor = TextLight,
            errorBorderColor = NeonError,
            focusedLabelColor = GradientCyan,
            unfocusedLabelColor = TextMuted
        )
    )
}

// ================================
// MAIN LOGIN SCREEN
// ================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuroraLoginScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userRepository = remember { UserRepository() }
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Student") }
    var passwordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    var loginData by remember { mutableStateOf<com.example.aplikasimonitoringkelas3.data.model.LoginData?>(null) }
    
    var showContent by remember { mutableStateOf(false) }
    val roles = listOf("Student", "Curriculum", "Principal", "Admin")
    val roleIcons = mapOf(
        "Student" to "📚", "Curriculum" to "📋", "Principal" to "👔", "Admin" to "⚙️"
    )
    val isEmailValid = remember(email) { Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    
    LaunchedEffect(Unit) { delay(200); showContent = true }
    
    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "main")
    val logoRotation by infiniteTransition.animateFloat(
        initialValue = -3f, targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logoRotation"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "glow"
    )
    
    // Success handler
    LaunchedEffect(showSuccess) {
        if (showSuccess && loginData != null) {
            delay(2000)
            val intent = when (selectedRole) {
                "Student" -> Intent(context, SiswaActivity::class.java)
                "Curriculum" -> Intent(context, KurikulumActivity::class.java)
                "Principal" -> Intent(context, KepalaSekolahActivity::class.java)
                "Admin" -> Intent(context, AdminActivity::class.java)
                else -> Intent(context, SiswaActivity::class.java)
            }
            intent.putExtra("USER_NAME", loginData?.user?.nama)
            intent.putExtra("USER_EMAIL", loginData?.user?.email)
            intent.putExtra("USER_ROLE", loginData?.user?.role)
            context.startActivity(intent)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
    ) {
        // Aurora background effects
        AuroraGlow()
        FloatingParticles()
        
        // Success Overlay
        AnimatedVisibility(
            visible = showSuccess,
            enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SpaceBlack.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Success ring animation
                    val successScale by animateFloatAsState(
                        targetValue = if (showSuccess) 1f else 0f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
                        label = "successScale"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(successScale)
                            .background(
                                brush = Brush.linearGradient(listOf(NeonSuccess, GradientCyan)),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", fontSize = 56.sp, color = SpaceBlack, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        "Welcome!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBright
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        loginData?.user?.nama ?: "",
                        fontSize = 20.sp,
                        color = GradientCyan
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { i ->
                            val dotAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f, targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    tween(600), RepeatMode.Reverse, StartOffset(i * 200)
                                ),
                                label = "dot$i"
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .alpha(dotAlpha)
                                    .background(GradientCyan, CircleShape)
                            )
                        }
                    }
                }
            }
        }
        
        // Main Login Content
        AnimatedVisibility(
            visible = !showSuccess,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                
                // Logo with glow effect
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(800)) + scaleIn(initialScale = 0.5f, animationSpec = spring(0.6f, 300f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Glow behind logo
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .blur(30.dp)
                                .alpha(glowPulse * 0.6f)
                                .background(
                                    brush = Brush.radialGradient(listOf(AuroraPurple, Color.Transparent)),
                                    shape = CircleShape
                                )
                        )
                        // Logo
                        Image(
                            painter = painterResource(id = R.drawable.logo_smenda),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(140.dp)
                                .rotate(logoRotation)
                                .clip(CircleShape)
                                .border(3.dp, Brush.linearGradient(listOf(GradientCyan, AuroraPurple)), CircleShape)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Title with gradient
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(600, 300)) + slideInVertically { it / 2 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "AURORA",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            style = LocalTextStyle.current.copy(
                                brush = Brush.linearGradient(listOf(GradientCyan, GradientBlue, AuroraPurple))
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Class Monitoring System",
                            fontSize = 14.sp,
                            color = TextMuted,
                            letterSpacing = 2.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Glass Login Card
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(600, 500)) + slideInVertically { it / 3 }
                ) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            // Role Selection - Horizontal chips
                            Text("Select Role", color = TextMuted, fontSize = 12.sp, letterSpacing = 1.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                roles.forEach { role ->
                                    val isSelected = selectedRole == role
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedRole = role },
                                        label = { 
                                            Text(
                                                "${roleIcons[role]} ${role.take(4)}",
                                                fontSize = 11.sp
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GradientBlue.copy(alpha = 0.3f),
                                            selectedLabelColor = GradientCyan,
                                            containerColor = Color.Transparent,
                                            labelColor = TextMuted
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            borderColor = if (isSelected) GradientCyan else GlassBorder,
                                            selectedBorderColor = GradientCyan,
                                            enabled = true,
                                            selected = isSelected
                                        )
                                    )
                                }
                            }
                            
                            // Email field
                            GlassTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = "Email Address",
                                leadingIcon = {
                                    Icon(Icons.Default.Email, null, tint = if (email.isEmpty()) TextMuted else GradientCyan)
                                },
                                isError = email.isNotEmpty() && !isEmailValid,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )
                            
                            // Email error
                            AnimatedVisibility(
                                visible = email.isNotEmpty() && !isEmailValid,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Text("Invalid email format", color = NeonError, fontSize = 12.sp)
                            }
                            
                            // Password field
                            GlassTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = "Password",
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, null, tint = if (password.isEmpty()) TextMuted else GradientCyan)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = if (passwordVisible) GradientCyan else TextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )
                            
                            // Error message
                            AnimatedVisibility(
                                visible = errorMessage != null,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(NeonError.copy(alpha = 0.15f))
                                        .border(1.dp, NeonError.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        errorMessage ?: "",
                                        color = NeonError,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Login Button
                            Button(
                                onClick = {
                                    scope.launch {
                                        isLoading = true
                                        errorMessage = null
                                        
                                        val result = userRepository.login(email, password)
                                        
                                        result.onSuccess { data ->
                                            isLoading = false
                                            val roleMap = mapOf(
                                                "Student" to "siswa",
                                                "Curriculum" to "kurikulum",
                                                "Principal" to "kepala sekolah",
                                                "Admin" to "admin"
                                            )
                                            val mappedRole = roleMap[selectedRole] ?: selectedRole
                                            
                                            if (data.user.role.equals(mappedRole, ignoreCase = true)) {
                                                loginData = data
                                                showSuccess = true
                                            } else {
                                                errorMessage = "Role mismatch! Your account is: ${data.user.role}"
                                            }
                                        }
                                        
                                        result.onFailure { error ->
                                            isLoading = false
                                            errorMessage = error.message ?: "Login failed"
                                        }
                                    }
                                },
                                enabled = isEmailValid && password.isNotEmpty() && !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = if (isEmailValid && password.isNotEmpty() && !isLoading)
                                                    listOf(GradientBlue, AuroraPurple)
                                                else
                                                    listOf(TextDark, TextDark)
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isLoading) {
                                        LoadingRing()
                                    } else {
                                        Text(
                                            "Sign In",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isEmailValid && password.isNotEmpty()) TextBright else TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Footer
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(600, 800))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "SMKN 2 Buduran Sidoarjo",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "© 2025 Aurora Monitoring",
                            color = TextDark,
                            fontSize = 10.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}