// ========================================
// FCM Service - Node.js Implementation
// ========================================
// File: services/fcm.service.js

const admin = require('../config/firebase');

/**
 * FCM Service untuk mengirim notifikasi menggunakan Firebase Admin SDK
 * 
 * Features:
 * - Send to single device
 * - Send to multiple devices (multicast)
 * - Send to topics
 * - Error handling dengan retry logic
 * - Token validation
 */
class FCMService {
  
  /**
   * Kirim notifikasi ke satu device berdasarkan FCM Token
   * 
   * @param {string} fcmToken - FCM Token dari device target
   * @param {string} title - Judul notifikasi
   * @param {string} body - Isi notifikasi
   * @param {object} data - Data payload untuk logika bisnis (opsional)
   * @returns {Promise<string>} Message ID jika berhasil
   * 
   * @example
   * await sendTargetedNotification(
   *   'fcmToken123...',
   *   'Pesanan Baru',
   *   'Anda mendapat pesanan dari John Doe',
   *   { order_id: '12345', type: 'new_order' }
   * );
   */
  async sendTargetedNotification(fcmToken, title, body, data = {}) {
    try {
      // Validasi input
      if (!fcmToken || !title || !body) {
        throw new Error('fcmToken, title, and body are required');
      }
      
      // Construct message payload
      const message = {
        // Notification payload - untuk menampilkan notifikasi di system tray
        notification: {
          title: title,
          body: body
        },
        
        // Data payload - untuk logika bisnis di app
        // PENTING: Semua values harus string (FCM requirement)
        data: {
          ...Object.keys(data).reduce((acc, key) => {
            acc[key] = String(data[key]);
            return acc;
          }, {}),
          timestamp: String(Date.now())
        },
        
        // Android specific configuration
        android: {
          priority: 'high',
          notification: {
            sound: 'default',
            color: '#FF6B6B',
            channelId: 'fcm_default_channel',
            icon: 'ic_notification'
          }
        },
        
        // APNs (iOS) specific configuration
        apns: {
          payload: {
            aps: {
              sound: 'default',
              badge: 1
            }
          }
        },
        
        // Target FCM Token
        token: fcmToken
      };
      
      // Kirim message ke FCM
      const response = await admin.messaging().send(message);
      
      console.log('✅ Successfully sent notification:', response);
      console.log('📱 Target token:', fcmToken);
      console.log('📬 Title:', title);
      console.log('📝 Body:', body);
      console.log('📦 Data:', data);
      
      return response; // Returns message ID (e.g., "projects/123/messages/456")
      
    } catch (error) {
      console.error('❌ Error sending notification:', error);
      
      // Handle specific FCM errors
      if (error.code === 'messaging/invalid-registration-token') {
        console.error('🗑️ Invalid FCM token format. Token should be removed from database.');
      } else if (error.code === 'messaging/registration-token-not-registered') {
        console.error('🗑️ FCM token is not registered. User may have uninstalled app.');
      } else if (error.code === 'messaging/invalid-argument') {
        console.error('⚠️ Invalid message payload. Check data types and format.');
      }
      
      throw error;
    }
  }
  
  /**
   * Kirim notifikasi ke multiple devices sekaligus (batch)
   * Lebih efisien daripada mengirim satu per satu
   * 
   * @param {Array<string>} fcmTokens - Array of FCM tokens (max 500 per call)
   * @param {string} title - Judul notifikasi
   * @param {string} body - Isi notifikasi
   * @param {object} data - Data payload
   * @returns {Promise<object>} Batch response dengan success dan failure counts
   * 
   * @example
   * const tokens = ['token1', 'token2', 'token3'];
   * const result = await sendMulticastNotification(
   *   tokens,
   *   'Flash Sale!',
   *   'Diskon 50% untuk semua produk',
   *   { type: 'promo', promo_id: '789' }
   * );
   * // result: { successCount: 2, failureCount: 1, responses: [...] }
   */
  async sendMulticastNotification(fcmTokens, title, body, data = {}) {
    try {
      // Validasi input
      if (!Array.isArray(fcmTokens) || fcmTokens.length === 0) {
        throw new Error('fcmTokens must be a non-empty array');
      }
      
      // FCM limit: maksimal 500 tokens per batch
      if (fcmTokens.length > 500) {
        throw new Error('Maximum 500 tokens per batch. Please split into multiple calls.');
      }
      
      // Construct multicast message
      const message = {
        notification: {
          title: title,
          body: body
        },
        data: {
          ...Object.keys(data).reduce((acc, key) => {
            acc[key] = String(data[key]);
            return acc;
          }, {}),
          timestamp: String(Date.now())
        },
        android: {
          priority: 'high',
          notification: {
            sound: 'default',
            color: '#FF6B6B',
            channelId: 'fcm_default_channel'
          }
        },
        tokens: fcmTokens
      };
      
      // Send multicast message
      const response = await admin.messaging().sendEachForMulticast(message);
      
      console.log(`✅ Successfully sent ${response.successCount} notifications`);
      console.log(`❌ Failed to send ${response.failureCount} notifications`);
      
      // Log failed tokens untuk cleanup
      if (response.failureCount > 0) {
        console.log('\n📋 Failed tokens:');
        response.responses.forEach((resp, idx) => {
          if (!resp.success) {
            console.error(`  - Token ${idx}: ${fcmTokens[idx]}`);
            console.error(`    Error: ${resp.error?.code} - ${resp.error?.message}`);
          }
        });
      }
      
      return {
        successCount: response.successCount,
        failureCount: response.failureCount,
        responses: response.responses
      };
      
    } catch (error) {
      console.error('❌ Error sending multicast notification:', error);
      throw error;
    }
  }
  
  /**
   * Kirim notifikasi ke topic (subscribe-based messaging)
   * Semua users yang subscribe ke topic akan menerima notifikasi
   * 
   * @param {string} topic - Topic name (tanpa "/topics/" prefix)
   * @param {string} title - Judul notifikasi
   * @param {string} body - Isi notifikasi
   * @param {object} data - Data payload
   * @returns {Promise<string>} Message ID
   * 
   * @example
   * await sendToTopic(
   *   'all_sellers',
   *   'Pengumuman Penting',
   *   'Sistem maintenance pada 1 Jan 2025',
   *   { type: 'announcement', priority: 'high' }
   * );
   */
  async sendToTopic(topic, title, body, data = {}) {
    try {
      if (!topic || !title || !body) {
        throw new Error('topic, title, and body are required');
      }
      
      const message = {
        notification: {
          title: title,
          body: body
        },
        data: {
          ...Object.keys(data).reduce((acc, key) => {
            acc[key] = String(data[key]);
            return acc;
          }, {}),
          timestamp: String(Date.now())
        },
        android: {
          priority: 'high',
          notification: {
            sound: 'default',
            color: '#FF6B6B'
          }
        },
        topic: topic
      };
      
      const response = await admin.messaging().send(message);
      
      console.log(`✅ Successfully sent notification to topic: ${topic}`);
      console.log('📬 Message ID:', response);
      
      return response;
      
    } catch (error) {
      console.error('❌ Error sending to topic:', error);
      throw error;
    }
  }
  
  /**
   * Kirim notifikasi dengan kustomisasi penuh (custom sound, icon, image, dll)
   * 
   * @param {string} fcmToken - FCM Token
   * @param {string} title - Title
   * @param {string} body - Body
   * @param {object} options - Additional options
   * @returns {Promise<string>} Message ID
   * 
   * @example
   * await sendCustomNotification(
   *   'fcmToken123',
   *   'New Message',
   *   'You have a new message from Alice',
   *   {
   *     imageUrl: 'https://example.com/image.jpg',
   *     sound: 'message_tone.mp3',
   *     color: '#00FF00',
   *     icon: 'ic_message',
   *     clickAction: 'OPEN_CHAT',
   *     data: { chat_id: '789', user_id: '123' }
   *   }
   * );
   */
  async sendCustomNotification(fcmToken, title, body, options = {}) {
    try {
      const message = {
        notification: {
          title: title,
          body: body,
          imageUrl: options.imageUrl // URL gambar (optional)
        },
        data: options.data || {},
        android: {
          priority: 'high',
          notification: {
            sound: options.sound || 'default',
            color: options.color || '#FF6B6B',
            icon: options.icon || 'ic_notification',
            channelId: options.channelId || 'fcm_default_channel',
            clickAction: options.clickAction,
            tag: options.tag // Untuk grouping notifications
          }
        },
        apns: {
          payload: {
            aps: {
              sound: options.sound || 'default',
              badge: options.badge || 1,
              category: options.category
            }
          }
        },
        token: fcmToken
      };
      
      const response = await admin.messaging().send(message);
      console.log('✅ Custom notification sent:', response);
      
      return response;
      
    } catch (error) {
      console.error('❌ Error sending custom notification:', error);
      throw error;
    }
  }
  
  /**
   * Subscribe FCM token(s) ke topic
   * 
   * @param {string|Array<string>} tokens - Single token or array of tokens
   * @param {string} topic - Topic name
   * @returns {Promise<object>} Response dengan success/failure counts
   */
  async subscribeToTopic(tokens, topic) {
    try {
      const tokenArray = Array.isArray(tokens) ? tokens : [tokens];
      const response = await admin.messaging().subscribeToTopic(tokenArray, topic);
      
      console.log(`✅ Successfully subscribed ${response.successCount} tokens to topic: ${topic}`);
      if (response.failureCount > 0) {
        console.log(`❌ Failed to subscribe ${response.failureCount} tokens`);
      }
      
      return response;
    } catch (error) {
      console.error('❌ Error subscribing to topic:', error);
      throw error;
    }
  }
  
  /**
   * Unsubscribe FCM token(s) dari topic
   * 
   * @param {string|Array<string>} tokens - Single token or array of tokens
   * @param {string} topic - Topic name
   * @returns {Promise<object>} Response dengan success/failure counts
   */
  async unsubscribeFromTopic(tokens, topic) {
    try {
      const tokenArray = Array.isArray(tokens) ? tokens : [tokens];
      const response = await admin.messaging().unsubscribeFromTopic(tokenArray, topic);
      
      console.log(`✅ Successfully unsubscribed ${response.successCount} tokens from topic: ${topic}`);
      if (response.failureCount > 0) {
        console.log(`❌ Failed to unsubscribe ${response.failureCount} tokens`);
      }
      
      return response;
    } catch (error) {
      console.error('❌ Error unsubscribing from topic:', error);
      throw error;
    }
  }
  
  /**
   * Send notification dengan retry logic (untuk handle transient errors)
   * 
   * @param {string} fcmToken - FCM Token
   * @param {string} title - Title
   * @param {string} body - Body
   * @param {object} data - Data payload
   * @param {number} maxRetries - Maximum retry attempts (default: 3)
   * @returns {Promise<object>} Result dengan success status dan response
   */
  async sendWithRetry(fcmToken, title, body, data = {}, maxRetries = 3) {
    let lastError;
    
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        console.log(`📤 Attempt ${attempt} of ${maxRetries}`);
        
        const result = await this.sendTargetedNotification(fcmToken, title, body, data);
        
        return { success: true, result };
        
      } catch (error) {
        lastError = error;
        
        // Don't retry for permanent errors
        const permanentErrors = [
          'messaging/invalid-registration-token',
          'messaging/registration-token-not-registered',
          'messaging/invalid-argument'
        ];
        
        if (permanentErrors.includes(error.code)) {
          console.error('❌ Permanent error, not retrying:', error.code);
          break;
        }
        
        // Exponential backoff untuk retry
        if (attempt < maxRetries) {
          const delay = Math.pow(2, attempt) * 1000; // 2s, 4s, 8s
          console.log(`⏳ Retry attempt ${attempt} failed. Waiting ${delay}ms before next attempt...`);
          await new Promise(resolve => setTimeout(resolve, delay));
        }
      }
    }
    
    console.error('❌ All retry attempts failed');
    return { success: false, error: lastError };
  }
}

// Export singleton instance
module.exports = new FCMService();
