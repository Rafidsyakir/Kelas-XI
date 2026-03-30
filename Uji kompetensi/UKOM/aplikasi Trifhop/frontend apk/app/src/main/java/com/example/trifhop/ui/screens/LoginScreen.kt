package com.example.trifhop.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trifhop.data.local.UserPreferences
import com.example.trifhop.data.model.GoogleLoginRequest
import com.example.trifhop.data.model.LoginRequest
import com.example.trifhop.data.network.RetrofitClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

// Web Client ID dari Google Cloud Console (OAuth 2.0 → Web application)
private const val GOOGLE_WEB_CLIENT_ID = "663024475354-vh4ib3j8l6bbl7omlcp7ocv6rvvbufo1.apps.googleusercontent.com"
private const val TAG = "LoginScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    userPreferences: UserPreferences
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Google Sign-In client — dibungkus runCatching agar tidak crash bila Play Services bermasalah
    val googleSignInClient = remember {
        runCatching {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                .requestEmail()
                .build()
            GoogleSignIn.getClient(context, gso)
        }.getOrNull()
    }

    // Launcher untuk hasil Google Sign-In
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isGoogleLoading = false
        if (result.resultCode == Activity.RESULT_OK) {
            runCatching {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken

                if (idToken != null) {
                    isGoogleLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            val response = RetrofitClient.apiService.googleLogin(
                                GoogleLoginRequest(idToken)
                            )
                            if (response.success && response.data != null) {
                                userPreferences.saveUser(response.data.user, response.data.token)
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                errorMessage = response.message ?: "Google login gagal"
                                googleSignInClient?.signOut()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Google network error", e)
                            errorMessage = "Gagal menghubungi server: ${e.localizedMessage}"
                            googleSignInClient?.signOut()
                        } finally {
                            isGoogleLoading = false
                        }
                    }
                } else {
                    errorMessage = "Gagal mendapatkan token Google. Pastikan koneksi internet aktif."
                }
            }.onFailure { e ->
                Log.e(TAG, "Google Sign-In failed", e)
                if (e is ApiException) {
                    val msg = when (e.statusCode) {
                        GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Login Google dibatalkan"
                        GoogleSignInStatusCodes.NETWORK_ERROR -> "Tidak ada koneksi internet"
                        10 -> "Konfigurasi Google belum diatur (DEVELOPER_ERROR). Gunakan login email."
                        else -> "Google Sign-In gagal (kode: ${e.statusCode})"
                    }
                    errorMessage = msg
                } else {
                    errorMessage = "Terjadi kesalahan saat login Google"
                }
                googleSignInClient?.signOut()
            }
        } else {
            // User membatalkan dialog atau terjadi error non-OK
            if (result.resultCode != Activity.RESULT_CANCELED) {
                errorMessage = "Google Sign-In tidak berhasil"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1723)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo Circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF137FEC), Color(0xFF0D5FB8))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Title
            Text(
                text = "Trifhop",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF137FEC)
            )
            Text(
                text = "Premium Thrift Experience",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2332))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Login to your account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Email", color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF137FEC))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF137FEC),
                            unfocusedBorderColor = Color(0xFF2D3748),
                            cursorColor = Color(0xFF137FEC)
                        )
                    )
                    
                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password", color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF137FEC))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility 
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) 
                            VisualTransformation.None 
                        else 
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF137FEC),
                            unfocusedBorderColor = Color(0xFF2D3748),
                            cursorColor = Color(0xFF137FEC)
                        )
                    )
                    
                    // Error Message
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFFF6B6B),
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Login Button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                return@Button
                            }
                            
                            isLoading = true
                            errorMessage = null
                            
                            scope.launch {
                                try {
                                    val response = RetrofitClient.apiService.login(
                                        LoginRequest(email, password)
                                    )
                                    
                                    if (response.success && response.data != null) {
                                        userPreferences.saveUser(response.data.user, response.data.token)
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        errorMessage = response.message ?: "Login failed"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Network error: ${e.localizedMessage}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF137FEC)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Login",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Register Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "Register",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF137FEC),
                            modifier = Modifier.clickable {
                                navController.navigate("register")
                            }
                        )
                    }

                    // Divider ATAU
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2D3748))
                        Text("ATAU", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2D3748))
                    }

                    // Google Sign-In Button
                    OutlinedButton(
                        onClick = {
                            if (googleSignInClient == null) {
                                errorMessage = "Google Sign-In tidak tersedia di perangkat ini"
                                return@OutlinedButton
                            }
                            isGoogleLoading = true
                            errorMessage = null
                            // Sign out dulu agar bisa pilih akun baru
                            googleSignInClient.signOut().addOnCompleteListener {
                                runCatching {
                                    googleLauncher.launch(googleSignInClient.signInIntent)
                                }.onFailure { e ->
                                    isGoogleLoading = false
                                    errorMessage = "Tidak dapat membuka Google Sign-In: ${e.localizedMessage}"
                                    Log.e(TAG, "Launch failed", e)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2D3748)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E293B)),
                        enabled = !isGoogleLoading && !isLoading
                    ) {
                        if (isGoogleLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("G", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4285F4))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Lanjutkan dengan Google", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }

                    // Skip Login (Guest)
                    TextButton(
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Continue as Guest",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}