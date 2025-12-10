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
        
        private var notificationId = 0
    }

    /**
     * Dipanggil ketika FCM Token baru dihasilkan atau di-refresh
     * Token ini unik untuk setiap device dan harus dikirim ke backend
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        Log.d(TAG, "📱 New FCM Token generated: $token")
        
        saveTokenLocally(token)
        sendTokenToBackend(token)
    }

    /**
     * Dipanggil ketika pesan FCM diterima
     * Akan dipanggil baik ketika app di foreground maupun background
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "📨 Message received from: ${remoteMessage.from}")
        
        // Handle notification payload
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "📬 Notification Title: ${notification.title}")
            Log.d(TAG, "📬 Notification Body: ${notification.body}")
            
            sendNotification(
                title = notification.title ?: "Notification",
                body = notification.body ?: "",
                imageUrl = notification.imageUrl?.toString()
            )
        }
        
        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "📦 Data Payload: ${remoteMessage.data}")
            handleDataPayload(remoteMessage.data)
        }
        
        // Jika hanya ada data tanpa notification, buat notifikasi manual
        if (remoteMessage.notification == null && remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "New Message"
            val body = remoteMessage.data["body"] ?: "You have a new message"
            sendNotification(title = title, body = body)
        }
    }

    /**
     * Menangani data payload untuk logika bisnis spesifik
     */
    private fun handleDataPayload(data: Map<String, String>) {
        when (data["type"]) {
            "new_order" -> {
                val orderId = data["order_id"]
                Log.d(TAG, "🛒 New Order Received: $orderId")
                // TODO: Handle new order logic
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
     */
    private fun sendNotification(
        title: String,
        body: String,
        imageUrl: String? = null
    ) {
        createNotificationChannel()
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("from_notification", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        
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
     * Placeholder - ganti dengan implementasi API call yang sebenarnya
     */
    private fun sendTokenToBackend(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🚀 Sending token to backend...")
                
                // TODO: Implementasi actual API call
                // Contoh:
                // val response = apiService.registerFcmToken(
                //     userId = getCurrentUserId(),
                //     fcmToken = token,
                //     deviceInfo = getDeviceInfo()
                // )
                
                // Placeholder
                simulateBackendCall(token)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error sending token to backend", e)
            }
        }
    }

    /**
     * Simulasi pengiriman token ke backend
     */
    private fun simulateBackendCall(token: String) {
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

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
        Log.d(TAG, "✅ Message sent successfully: $msgId")
    }

    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
        Log.e(TAG, "❌ Error sending message: $msgId", exception)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d(TAG, "🗑️ Messages deleted on server")
    }
}
