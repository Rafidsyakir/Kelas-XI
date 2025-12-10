# 📱 Firebase Cloud Messaging (FCM) - Android Implementation Guide

Panduan lengkap implementasi Firebase Cloud Messaging untuk aplikasi Android dengan Kotlin.

---

## 📋 Daftar Isi

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Konfigurasi Gradle](#konfigurasi-gradle)
4. [Implementasi MyFirebaseMessagingService](#implementasi-myfirebasemessagingservice)
5. [Notification Helper](#notification-helper)
6. [Permission Handling (Android 13+)](#permission-handling-android-13)
7. [MainActivity Implementation](#mainactivity-implementation)
8. [AndroidManifest Configuration](#androidmanifest-configuration)
9. [Testing FCM](#testing-fcm)
10. [Troubleshooting](#troubleshooting)

---

## Overview

Firebase Cloud Messaging (FCM) memungkinkan aplikasi untuk:
- 📨 Menerima notifikasi push dari server
- 🔑 Mendapatkan FCM Token unik untuk setiap perangkat
- 🔔 Menampilkan notifikasi sistem
- 📊 Mengirim data payload untuk logika bisnis
- 🎯 Target spesifik user berdasarkan token

---

## Prerequisites

Sebelum memulai, pastikan:

- ✅ Proyek Firebase sudah dibuat
- ✅ File `google-services.json` sudah ditambahkan ke folder `app/`
- ✅ Plugin `google-services` sudah dikonfigurasi
- ✅ Android Studio terbaru
- ✅ minSdk minimal 24 (Android 7.0)

---

## Konfigurasi Gradle

### 1. build.gradle.kts (Project Level)

```kotlin
// build.gradle.kts (Project Level)
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}
```

### 2. build.gradle.kts (Module: app)

Tambahkan dependensi FCM di `app/build.gradle.kts`:

```kotlin
// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.firebasetutorial"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.firebasetutorial"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    
    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging-ktx")
    
    // Firebase Analytics (optional but recommended)
    implementation("com.google.firebase:firebase-analytics-ktx")
    
    // Coroutines untuk async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}
```

### 3. Sync Gradle

Setelah menambahkan dependensi, sync project:

```
File → Sync Project with Gradle Files
```

---

## Implementasi MyFirebaseMessagingService

Buat file `MyFirebaseMessagingService.kt` di package utama:

### File: MyFirebaseMessagingService.kt

```kotlin
package com.example.firebasetutorial

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Service untuk menangani Firebase Cloud Messaging
 * Extends FirebaseMessagingService untuk menerima pesan FCM
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "fcm_default_channel"
        private const val CHANNEL_NAME = "FCM Notifications"
        private const val CHANNEL_DESCRIPTION = "Firebase Cloud Messaging Notifications"
        
        // Notification ID untuk tracking
        private var notificationId = 0
    }

    /**
     * Dipanggil ketika FCM Token baru dihasilkan atau di-refresh
     * Token ini unik untuk setiap device dan harus dikirim ke backend
     * 
     * @param token FCM Token yang baru dihasilkan
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        Log.d(TAG, "📱 New FCM Token generated: $token")
        
        // Simpan token secara lokal untuk referensi
        saveTokenLocally(token)
        
        // Kirim token ke backend server
        sendTokenToBackend(token)
    }

    /**
     * Dipanggil ketika pesan FCM diterima
     * Akan dipanggil baik ketika app di foreground maupun background
     * 
     * @param remoteMessage Objek yang berisi data pesan dari FCM
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "📨 Message received from: ${remoteMessage.from}")
        
        // Check apakah pesan berisi notification payload
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "📬 Notification Title: ${notification.title}")
            Log.d(TAG, "📬 Notification Body: ${notification.body}")
            
            // Tampilkan notifikasi sistem
            sendNotification(
                title = notification.title ?: "Notification",
                body = notification.body ?: "",
                imageUrl = notification.imageUrl?.toString()
            )
        }
        
        // Check apakah pesan berisi data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "📦 Data Payload: ${remoteMessage.data}")
            
            // Handle data payload untuk logika bisnis
            handleDataPayload(remoteMessage.data)
        }
        
        // Jika tidak ada notification tapi ada data, buat notifikasi custom
        if (remoteMessage.notification == null && remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "New Message"
            val body = remoteMessage.data["body"] ?: "You have a new message"
            
            sendNotification(title = title, body = body)
        }
    }

    /**
     * Menangani data payload untuk logika bisnis spesifik
     * Misalnya: navigasi ke screen tertentu, update data, dll
     * 
     * @param data Map berisi key-value data payload
     */
    private fun handleDataPayload(data: Map<String, String>) {
        // Contoh handling berdasarkan tipe notifikasi
        when (data["type"]) {
            "new_order" -> {
                val orderId = data["order_id"]
                Log.d(TAG, "🛒 New Order Received: $orderId")
                // TODO: Handle new order logic
                // Misalnya: update UI, simpan ke database, dll
            }
            
            "order_status" -> {
                val orderId = data["order_id"]
                val status = data["status"]
                Log.d(TAG, "📦 Order Status Update: Order #$orderId - $status")
                // TODO: Handle order status update
            }
            
            "message" -> {
                val senderId = data["sender_id"]
                val messageText = data["message"]
                Log.d(TAG, "💬 New Message from $senderId: $messageText")
                // TODO: Handle new message
            }
            
            else -> {
                Log.d(TAG, "ℹ️ Unknown notification type or generic data payload")
            }
        }
    }

    /**
     * Menampilkan notifikasi sistem
     * Membuat notification channel untuk Android 8.0+ (API 26+)
     * 
     * @param title Judul notifikasi
     * @param body Isi/body notifikasi
     * @param imageUrl URL gambar untuk big picture style (optional)
     */
    private fun sendNotification(
        title: String,
        body: String,
        imageUrl: String? = null
    ) {
        // Buat notification channel terlebih dahulu (wajib untuk Android 8.0+)
        createNotificationChannel()
        
        // Intent yang akan dibuka ketika notifikasi di-tap
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Tambahkan extra data jika diperlukan
            putExtra("from_notification", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Build notifikasi
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Ganti dengan icon Anda
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Notifikasi hilang setelah di-tap
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // Support text panjang
        
        // Jika ada image URL, gunakan BigPictureStyle
        imageUrl?.let {
            // TODO: Load image dari URL dan set ke notification
            // Gunakan library seperti Glide atau Coil untuk load image
            Log.d(TAG, "🖼️ Image URL provided: $imageUrl")
        }
        
        // Tampilkan notifikasi
        try {
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(notificationId++, notificationBuilder.build())
            
            Log.d(TAG, "✅ Notification displayed successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ Notification permission denied", e)
        }
    }

    /**
     * Membuat Notification Channel untuk Android 8.0+ (API 26+)
     * Channel diperlukan untuk menampilkan notifikasi di Android versi baru
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            
            Log.d(TAG, "📢 Notification channel created: $CHANNEL_ID")
        }
    }

    /**
     * Menyimpan FCM Token secara lokal menggunakan SharedPreferences
     * Token ini dapat digunakan untuk keperluan lain dalam aplikasi
     * 
     * @param token FCM Token yang akan disimpan
     */
    private fun saveTokenLocally(token: String) {
        val sharedPreferences = getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("fcm_token", token)
            putLong("token_timestamp", System.currentTimeMillis())
            apply()
        }
        
        Log.d(TAG, "💾 Token saved locally")
    }

    /**
     * Mengirim FCM Token ke backend server
     * Ini adalah placeholder - implementasi sebenarnya tergantung pada API backend
     * 
     * @param token FCM Token yang akan dikirim
     */
    private fun sendTokenToBackend(token: String) {
        // Gunakan Coroutine untuk operasi async
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🚀 Sending token to backend...")
                
                // TODO: Implementasi actual API call ke backend
                // Contoh menggunakan Retrofit atau library networking lainnya:
                /*
                val response = apiService.registerFcmToken(
                    userId = getCurrentUserId(),
                    fcmToken = token,
                    deviceInfo = getDeviceInfo()
                )
                
                if (response.isSuccessful) {
                    Log.d(TAG, "✅ Token successfully sent to backend")
                } else {
                    Log.e(TAG, "❌ Failed to send token: ${response.errorBody()}")
                }
                */
                
                // Placeholder implementation
                simulateBackendCall(token)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error sending token to backend", e)
                // Implement retry logic jika diperlukan
            }
        }
    }

    /**
     * Simulasi pengiriman token ke backend
     * Ganti dengan implementasi API call yang sebenarnya
     */
    private fun simulateBackendCall(token: String) {
        // Simulasi delay network
        Thread.sleep(1000)
        
        Log.d(TAG, """
            ========================================
            📤 TOKEN SENT TO BACKEND (SIMULATED)
            ========================================
            Token: $token
            Timestamp: ${System.currentTimeMillis()}
            Device: ${Build.MODEL}
            Android Version: ${Build.VERSION.RELEASE}
            ========================================
        """.trimIndent())
    }

    /**
     * Dipanggil ketika pesan tidak dapat dikirim
     * Useful untuk debugging
     */
    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
        Log.d(TAG, "✅ Message sent successfully: $msgId")
    }

    /**
     * Dipanggil ketika ada error dalam pengiriman pesan
     */
    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
        Log.e(TAG, "❌ Error sending message: $msgId", exception)
    }

    /**
     * Dipanggil ketika pesan dihapus dari server
     */
    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d(TAG, "🗑️ Messages deleted on server")
    }
}
```

---

## Notification Helper

Jika Anda ingin memisahkan logika notifikasi, buat helper class terpisah:

### File: NotificationHelper.kt

```kotlin
package com.example.firebasetutorial

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Helper class untuk menangani notifikasi sistem
 * Memisahkan logika notifikasi dari FirebaseMessagingService
 */
object NotificationHelper {

    private const val CHANNEL_ID = "fcm_default_channel"
    private const val CHANNEL_NAME = "FCM Notifications"
    private const val CHANNEL_DESCRIPTION = "Firebase Cloud Messaging Notifications"
    
    private var notificationId = 0

    /**
     * Membuat dan menampilkan notifikasi
     */
    fun showNotification(
        context: Context,
        title: String,
        body: String,
        intent: Intent? = null
    ) {
        createNotificationChannel(context)
        
        val defaultIntent = intent ?: Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            defaultIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(notificationId++, notification)
        } catch (e: SecurityException) {
            // Permission denied
        }
    }

    /**
     * Membuat notification channel untuk Android 8.0+
     */
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = 
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
```

---

## Permission Handling (Android 13+)

### File: MainActivity.kt

```kotlin
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
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    
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
                    // Kemudian request permission
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
                    
                    // Kirim ke backend (optional)
                    // sendTokenToBackend(token)
                    
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
```

---

## AndroidManifest Configuration

### File: AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Permission untuk internet (required untuk FCM) -->
    <uses-permission android:name="android.permission.INTERNET" />
    
    <!-- Permission untuk notifikasi (Android 13+) -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    
    <!-- Permission untuk vibration (optional) -->
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.FirebaseTutorial"
        tools:targetApi="31">
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Firebase Messaging Service -->
        <service
            android:name=".MyFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>

        <!-- 
            Default notification icon dan color
            Akan digunakan jika tidak ada custom notification
        -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/ic_notification" />
        
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_color"
            android:resource="@color/notification_color" />
        
        <!--
            Auto-init FCM
            Set false jika ingin manual initialization
        -->
        <meta-data
            android:name="firebase_messaging_auto_init_enabled"
            android:value="true" />
        
        <meta-data
            android:name="firebase_analytics_collection_enabled"
            android:value="true" />

    </application>

</manifest>
```

---

## Testing FCM

### 1. Test dari Firebase Console

1. Buka Firebase Console
2. Go to **Cloud Messaging** (atau **Engage → Messaging**)
3. Click **Send your first message**
4. Masukkan:
   - **Notification title**: Test Title
   - **Notification text**: Test Body
5. Click **Send test message**
6. Paste FCM Token dari log
7. Click **Test**

### 2. Test menggunakan curl

```bash
# Replace dengan Server Key dari Firebase Console
SERVER_KEY="your-server-key"
FCM_TOKEN="fcm-token-from-device"

curl -X POST https://fcm.googleapis.com/fcm/send \
  -H "Authorization: key=$SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "'$FCM_TOKEN'",
    "notification": {
      "title": "Test Notification",
      "body": "This is a test message"
    },
    "data": {
      "type": "test",
      "custom_key": "custom_value"
    }
  }'
```

### 3. Log yang Diharapkan

Ketika menerima notifikasi, log akan seperti:

```
D/FCMService: 📨 Message received from: 1234567890
D/FCMService: 📬 Notification Title: Test Notification
D/FCMService: 📬 Notification Body: This is a test message
D/FCMService: 📦 Data Payload: {type=test, custom_key=custom_value}
D/FCMService: ✅ Notification displayed successfully
```

---

## Troubleshooting

### Token tidak muncul

**Solusi:**
```kotlin
// Check di logcat untuk error
// Pastikan google-services.json sudah benar
// Pastikan package name match dengan Firebase
```

### Notifikasi tidak muncul

**Solusi:**
```kotlin
// 1. Check permission di Android 13+
// 2. Verify notification channel created
// 3. Check battery optimization
// 4. Test dengan app di foreground dulu
```

### Permission tidak diminta

**Solusi:**
```kotlin
// Pastikan targetSdk >= 33
// Uninstall dan install ulang app
// Check AndroidManifest.xml untuk POST_NOTIFICATIONS
```

### Token tidak dikirim ke backend

**Solusi:**
```kotlin
// Implement actual API call di sendTokenToBackend()
// Add retry logic untuk network error
// Save token locally untuk sync later
```

---

## Next Steps

1. ✅ Implement actual backend API call di `sendTokenToBackend()`
2. ✅ Add retry logic untuk token registration
3. ✅ Implement token refresh mechanism
4. ✅ Add analytics untuk track notification engagement
5. ✅ Handle deep linking dari notification
6. ✅ Implement notification grouping
7. ✅ Add custom sound untuk notification

---

**Dokumentasi lengkap! FCM sudah siap digunakan! 🎉**

*Next: Lihat FCM_BACKEND_GUIDE.md untuk implementasi backend*
