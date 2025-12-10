package com.example.firebasetutorial

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var btnGetToken: Button
    private lateinit var btnRequestPermission: Button
    private lateinit var tvToken: TextView
    private lateinit var tvPermissionStatus: TextView

    /**
     * Request permission launcher untuk Android 13+ (API 33+)
     * Menggunakan Activity Result API yang modern
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "✅ Notification permission granted")
            Toast.makeText(this, "✅ Permission granted", Toast.LENGTH_SHORT).show()
            updatePermissionStatus()
            
            // Setelah permission granted, dapatkan FCM token
            getFCMToken()
        } else {
            Log.d(TAG, "❌ Notification permission denied")
            Toast.makeText(this, "❌ Permission denied", Toast.LENGTH_SHORT).show()
            updatePermissionStatus()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize Firebase Analytics
        firebaseAnalytics = Firebase.analytics
        
        initViews()
        setupListeners()
        
        // Check dan request permission jika diperlukan
        checkAndRequestNotificationPermission()
        
        // Update status permission
        updatePermissionStatus()
        
        // Load token yang sudah ada (jika ada)
        loadSavedToken()
    }

    /**
     * Inisialisasi views
     */
    private fun initViews() {
        btnGetToken = findViewById(R.id.btnGetToken)
        btnRequestPermission = findViewById(R.id.btnRequestPermission)
        tvToken = findViewById(R.id.tvToken)
        tvPermissionStatus = findViewById(R.id.tvPermissionStatus)
    }

    /**
     * Setup click listeners
     */
    private fun setupListeners() {
        btnGetToken.setOnClickListener {
            getFCMToken()
        }
        
        btnRequestPermission.setOnClickListener {
            requestNotificationPermission()
        }
    }

    /**
     * Check permission dan request jika belum granted
     * Khusus untuk Android 13+ (API 33+)
     */
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "✅ Notification permission already granted")
                    // Permission sudah granted
                }
                
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Log.d(TAG, "ℹ️ Should show permission rationale")
                    // Tampilkan penjelasan mengapa permission diperlukan
                    showPermissionRationale()
                }
                
                else -> {
                    Log.d(TAG, "📱 Requesting notification permission")
                    // Langsung request permission
                    requestNotificationPermission()
                }
            }
        } else {
            // Android 12 ke bawah tidak perlu runtime permission untuk notifikasi
            Log.d(TAG, "✅ Android version < 13, notification permission not required")
        }
    }

    /**
     * Request notification permission
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /**
     * Tampilkan dialog penjelasan permission
     */
    private fun showPermissionRationale() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app needs notification permission to receive important updates and alerts.")
            .setPositiveButton("Grant") { _, _ ->
                requestNotificationPermission()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Update status permission di UI
     */
    private fun updatePermissionStatus() {
        val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Android 12 ke bawah selalu granted
        }
        
        val statusText = if (isGranted) {
            "✅ Notification Permission: GRANTED"
        } else {
            "❌ Notification Permission: DENIED"
        }
        
        tvPermissionStatus.text = statusText
        
        // Enable/disable button berdasarkan status
        btnGetToken.isEnabled = isGranted
    }

    /**
     * Mendapatkan FCM Token
     */
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "📱 FCM Token: $token")
                    
                    // Tampilkan token di UI
                    displayToken(token)
                    
                    // Simpan token
                    saveToken(token)
                    
                    Toast.makeText(this, "✅ Token retrieved", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "❌ Failed to get token", task.exception)
                    Toast.makeText(this, "❌ Failed to get token", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Tampilkan token di UI
     */
    private fun displayToken(token: String) {
        // Tampilkan token (dipotong untuk UI)
        val displayToken = if (token.length > 50) {
            "${token.take(25)}...${token.takeLast(25)}"
        } else {
            token
        }
        
        tvToken.text = "Token: $displayToken\n\nFull token logged in console"
        
        // Log full token
        Log.d(TAG, """
            ========================================
            📱 FCM TOKEN
            ========================================
            $token
            ========================================
            Copy this token to test FCM from console or backend
        """.trimIndent())
    }

    /**
     * Simpan token ke SharedPreferences
     */
    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("fcm_prefs", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("fcm_token", token)
            putLong("token_timestamp", System.currentTimeMillis())
            apply()
        }
    }

    /**
     * Load token yang sudah disimpan
     */
    private fun loadSavedToken() {
        val sharedPreferences = getSharedPreferences("fcm_prefs", MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("fcm_token", null)
        
        if (savedToken != null) {
            displayToken(savedToken)
        } else {
            tvToken.text = "No token yet. Click 'Get FCM Token' button."
        }
    }
}
