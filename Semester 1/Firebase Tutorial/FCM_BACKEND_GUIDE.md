# 🔥 Firebase Cloud Messaging - Backend Implementation Guide

Panduan lengkap implementasi FCM di backend menggunakan Firebase Admin SDK untuk mengirim notifikasi ke aplikasi Android.

---

## 📋 Daftar Isi

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Setup Firebase Admin SDK](#setup-firebase-admin-sdk)
4. [Service Account Key](#service-account-key)
5. [Node.js Implementation](#nodejs-implementation)
6. [Python Implementation](#python-implementation)
7. [PHP Implementation](#php-implementation)
8. [Use Case: E-Commerce](#use-case-e-commerce)
9. [Best Practices](#best-practices)
10. [Troubleshooting](#troubleshooting)

---

## Overview

Firebase Admin SDK memungkinkan backend untuk:
- 🔐 **Secure Authentication** - Menggunakan Service Account untuk auth
- 📨 **Send Notifications** - Kirim notifikasi ke device spesifik atau topics
- 🎯 **Target Specific Users** - Kirim ke FCM token tertentu
- 📊 **Data Payload** - Kirim data untuk logika bisnis
- 🔄 **Batch Sending** - Kirim ke multiple devices sekaligus

---

## Prerequisites

Sebelum memulai, pastikan:

- ✅ Proyek Firebase sudah dibuat
- ✅ Aplikasi Android sudah terdaftar di Firebase
- ✅ Backend server sudah siap (Node.js, Python, PHP, dll)
- ✅ Access ke Firebase Console

---

## Setup Firebase Admin SDK

### Node.js

```bash
npm install firebase-admin
```

### Python

```bash
pip install firebase-admin
```

### PHP

```bash
composer require kreait/firebase-php
```

### Java/Kotlin (Spring Boot)

```gradle
implementation("com.google.firebase:firebase-admin:9.2.0")
```

---

## Service Account Key

### 1. Download Service Account Key

1. Buka **Firebase Console** → Pilih proyek Anda
2. Go to **Project Settings** (⚙️ icon)
3. Tab **Service accounts**
4. Click **Generate new private key**
5. Download file JSON (contoh: `firebase-service-account.json`)

⚠️ **PENTING**: 
- File ini berisi kredensial sensitive
- **JANGAN commit ke Git**
- Simpan dengan aman
- Gunakan environment variables untuk path

### 2. Struktur Service Account Key

```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "your-private-key-id",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...",
  "client_email": "firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com",
  "client_id": "your-client-id",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/..."
}
```

---

## Node.js Implementation

### 1. Setup & Initialization

#### File: `config/firebase.js`

```javascript
// config/firebase.js
const admin = require('firebase-admin');
const path = require('path');

/**
 * Initialize Firebase Admin SDK
 * Uses service account key for authentication
 */
function initializeFirebase() {
  try {
    // Path ke service account key
    // Best practice: gunakan environment variable
    const serviceAccountPath = process.env.FIREBASE_SERVICE_ACCOUNT_PATH || 
                               path.join(__dirname, '../firebase-service-account.json');
    
    const serviceAccount = require(serviceAccountPath);
    
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      // Optional: jika menggunakan Realtime Database atau Storage
      databaseURL: `https://${serviceAccount.project_id}.firebaseio.com`,
      storageBucket: `${serviceAccount.project_id}.appspot.com`
    });
    
    console.log('✅ Firebase Admin SDK initialized successfully');
    
  } catch (error) {
    console.error('❌ Error initializing Firebase Admin SDK:', error);
    throw error;
  }
}

// Initialize saat module di-load
initializeFirebase();

module.exports = admin;
```

### 2. FCM Service

#### File: `services/fcm.service.js`

```javascript
// services/fcm.service.js
const admin = require('../config/firebase');

/**
 * FCM Service untuk mengirim notifikasi
 */
class FCMService {
  
  /**
   * Kirim notifikasi ke satu device
   * 
   * @param {string} fcmToken - FCM Token dari device target
   * @param {string} title - Judul notifikasi
   * @param {string} body - Isi notifikasi
   * @param {object} data - Data payload untuk logika bisnis
   * @returns {Promise<string>} Message ID jika berhasil
   */
  async sendTargetedNotification(fcmToken, title, body, data = {}) {
    try {
      // Validasi input
      if (!fcmToken || !title || !body) {
        throw new Error('fcmToken, title, and body are required');
      }
      
      // Construct message payload
      const message = {
        // Notification payload - untuk menampilkan notifikasi
        notification: {
          title: title,
          body: body
        },
        
        // Data payload - untuk logika bisnis di app
        data: {
          // Convert semua value ke string (FCM requirement)
          ...Object.keys(data).reduce((acc, key) => {
            acc[key] = String(data[key]);
            return acc;
          }, {}),
          // Tambahkan timestamp
          timestamp: String(Date.now())
        },
        
        // Android specific options
        android: {
          priority: 'high',
          notification: {
            sound: 'default',
            color: '#FF6B6B',
            channelId: 'fcm_default_channel'
          }
        },
        
        // APNs (iOS) specific options
        apns: {
          payload: {
            aps: {
              sound: 'default',
              badge: 1
            }
          }
        },
        
        // FCM Token target
        token: fcmToken
      };
      
      // Kirim message
      const response = await admin.messaging().send(message);
      
      console.log('✅ Successfully sent notification:', response);
      console.log('📱 Target token:', fcmToken);
      console.log('📬 Title:', title);
      console.log('📝 Body:', body);
      console.log('📦 Data:', data);
      
      return response; // Returns message ID
      
    } catch (error) {
      console.error('❌ Error sending notification:', error);
      
      // Handle specific errors
      if (error.code === 'messaging/invalid-registration-token' ||
          error.code === 'messaging/registration-token-not-registered') {
        console.error('🗑️ Invalid or unregistered FCM token. Token should be removed from database.');
      }
      
      throw error;
    }
  }
  
  /**
   * Kirim notifikasi ke multiple devices
   * 
   * @param {Array<string>} fcmTokens - Array of FCM tokens
   * @param {string} title - Judul notifikasi
   * @param {string} body - Isi notifikasi
   * @param {object} data - Data payload
   * @returns {Promise<object>} Batch response dengan success dan failure counts
   */
  async sendMulticastNotification(fcmTokens, title, body, data = {}) {
    try {
      if (!Array.isArray(fcmTokens) || fcmTokens.length === 0) {
        throw new Error('fcmTokens must be a non-empty array');
      }
      
      // Maksimal 500 tokens per batch
      if (fcmTokens.length > 500) {
        throw new Error('Maximum 500 tokens per batch');
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
        tokens: fcmTokens
      };
      
      const response = await admin.messaging().sendEachForMulticast(message);
      
      console.log(`✅ Successfully sent ${response.successCount} notifications`);
      console.log(`❌ Failed to send ${response.failureCount} notifications`);
      
      // Log failed tokens
      if (response.failureCount > 0) {
        response.responses.forEach((resp, idx) => {
          if (!resp.success) {
            console.error(`Failed token ${idx}:`, fcmTokens[idx], resp.error);
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
   * Kirim notifikasi ke topic
   * 
   * @param {string} topic - Topic name (tanpa "/topics/" prefix)
   * @param {string} title - Judul notifikasi
   * @param {string} body - Isi notifikasi
   * @param {object} data - Data payload
   * @returns {Promise<string>} Message ID
   */
  async sendToTopic(topic, title, body, data = {}) {
    try {
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
   * Kirim notifikasi dengan custom sound dan icon
   * 
   * @param {string} fcmToken - FCM Token
   * @param {string} title - Title
   * @param {string} body - Body
   * @param {object} options - Additional options
   * @returns {Promise<string>} Message ID
   */
  async sendCustomNotification(fcmToken, title, body, options = {}) {
    try {
      const message = {
        notification: {
          title: title,
          body: body,
          imageUrl: options.imageUrl // Optional image
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
            tag: options.tag // For grouping notifications
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
   * Subscribe token ke topic
   */
  async subscribeToTopic(tokens, topic) {
    try {
      const response = await admin.messaging().subscribeToTopic(tokens, topic);
      console.log(`✅ Successfully subscribed to topic ${topic}:`, response);
      return response;
    } catch (error) {
      console.error('❌ Error subscribing to topic:', error);
      throw error;
    }
  }
  
  /**
   * Unsubscribe token dari topic
   */
  async unsubscribeFromTopic(tokens, topic) {
    try {
      const response = await admin.messaging().unsubscribeFromTopic(tokens, topic);
      console.log(`✅ Successfully unsubscribed from topic ${topic}:`, response);
      return response;
    } catch (error) {
      console.error('❌ Error unsubscribing from topic:', error);
      throw error;
    }
  }
}

module.exports = new FCMService();
```

### 3. API Controller

#### File: `controllers/notification.controller.js`

```javascript
// controllers/notification.controller.js
const fcmService = require('../services/fcm.service');

class NotificationController {
  
  /**
   * POST /api/notifications/send
   * Send notification to single device
   */
  async sendNotification(req, res) {
    try {
      const { fcmToken, title, body, data } = req.body;
      
      // Validation
      if (!fcmToken || !title || !body) {
        return res.status(400).json({
          success: false,
          message: 'fcmToken, title, and body are required'
        });
      }
      
      const messageId = await fcmService.sendTargetedNotification(
        fcmToken,
        title,
        body,
        data
      );
      
      res.status(200).json({
        success: true,
        message: 'Notification sent successfully',
        messageId: messageId
      });
      
    } catch (error) {
      console.error('Error in sendNotification:', error);
      res.status(500).json({
        success: false,
        message: 'Failed to send notification',
        error: error.message
      });
    }
  }
  
  /**
   * POST /api/notifications/send-multiple
   * Send notification to multiple devices
   */
  async sendMultipleNotifications(req, res) {
    try {
      const { fcmTokens, title, body, data } = req.body;
      
      if (!Array.isArray(fcmTokens) || fcmTokens.length === 0) {
        return res.status(400).json({
          success: false,
          message: 'fcmTokens must be a non-empty array'
        });
      }
      
      const result = await fcmService.sendMulticastNotification(
        fcmTokens,
        title,
        body,
        data
      );
      
      res.status(200).json({
        success: true,
        message: 'Notifications sent',
        ...result
      });
      
    } catch (error) {
      console.error('Error in sendMultipleNotifications:', error);
      res.status(500).json({
        success: false,
        message: 'Failed to send notifications',
        error: error.message
      });
    }
  }
  
  /**
   * POST /api/notifications/send-to-topic
   * Send notification to topic
   */
  async sendToTopic(req, res) {
    try {
      const { topic, title, body, data } = req.body;
      
      if (!topic || !title || !body) {
        return res.status(400).json({
          success: false,
          message: 'topic, title, and body are required'
        });
      }
      
      const messageId = await fcmService.sendToTopic(topic, title, body, data);
      
      res.status(200).json({
        success: true,
        message: 'Notification sent to topic',
        messageId: messageId
      });
      
    } catch (error) {
      console.error('Error in sendToTopic:', error);
      res.status(500).json({
        success: false,
        message: 'Failed to send notification to topic',
        error: error.message
      });
    }
  }
}

module.exports = new NotificationController();
```

### 4. Routes

#### File: `routes/notification.routes.js`

```javascript
// routes/notification.routes.js
const express = require('express');
const router = express.Router();
const notificationController = require('../controllers/notification.controller');

// Send to single device
router.post('/send', notificationController.sendNotification);

// Send to multiple devices
router.post('/send-multiple', notificationController.sendMultipleNotifications);

// Send to topic
router.post('/send-to-topic', notificationController.sendToTopic);

module.exports = router;
```

### 5. Main App

#### File: `app.js` or `server.js`

```javascript
// app.js
const express = require('express');
const app = express();
require('./config/firebase'); // Initialize Firebase

// Middleware
app.use(express.json());

// Routes
app.use('/api/notifications', require('./routes/notification.routes'));

// Test route
app.get('/health', (req, res) => {
  res.json({ status: 'OK', message: 'FCM Backend is running' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`🚀 Server running on port ${PORT}`);
});
```

### 6. Example Usage

```javascript
// Example: Send notification
const fcmService = require('./services/fcm.service');

async function sendWelcomeNotification(userId, fcmToken) {
  try {
    await fcmService.sendTargetedNotification(
      fcmToken,
      'Welcome! 👋',
      'Thank you for registering!',
      {
        type: 'welcome',
        user_id: userId
      }
    );
  } catch (error) {
    console.error('Failed to send welcome notification:', error);
  }
}
```

---

## Python Implementation

### 1. Setup & Initialization

#### File: `firebase_config.py`

```python
# firebase_config.py
import firebase_admin
from firebase_admin import credentials, messaging
import os

def initialize_firebase():
    """Initialize Firebase Admin SDK"""
    try:
        # Path ke service account key
        service_account_path = os.getenv(
            'FIREBASE_SERVICE_ACCOUNT_PATH',
            'firebase-service-account.json'
        )
        
        cred = credentials.Certificate(service_account_path)
        firebase_admin.initialize_app(cred)
        
        print('✅ Firebase Admin SDK initialized successfully')
        
    except Exception as error:
        print(f'❌ Error initializing Firebase Admin SDK: {error}')
        raise error

# Initialize saat module di-import
initialize_firebase()
```

### 2. FCM Service

#### File: `fcm_service.py`

```python
# fcm_service.py
from firebase_admin import messaging
from typing import Dict, List, Optional
import time

class FCMService:
    """FCM Service untuk mengirim notifikasi"""
    
    def send_targeted_notification(
        self,
        fcm_token: str,
        title: str,
        body: str,
        data: Optional[Dict[str, str]] = None
    ) -> str:
        """
        Kirim notifikasi ke satu device
        
        Args:
            fcm_token: FCM Token dari device target
            title: Judul notifikasi
            body: Isi notifikasi
            data: Data payload untuk logika bisnis
            
        Returns:
            str: Message ID jika berhasil
        """
        try:
            # Validasi input
            if not fcm_token or not title or not body:
                raise ValueError('fcm_token, title, and body are required')
            
            # Convert data values ke string
            if data is None:
                data = {}
            
            data_payload = {
                **{k: str(v) for k, v in data.items()},
                'timestamp': str(int(time.time() * 1000))
            }
            
            # Construct message
            message = messaging.Message(
                notification=messaging.Notification(
                    title=title,
                    body=body
                ),
                data=data_payload,
                android=messaging.AndroidConfig(
                    priority='high',
                    notification=messaging.AndroidNotification(
                        sound='default',
                        color='#FF6B6B',
                        channel_id='fcm_default_channel'
                    )
                ),
                apns=messaging.APNSConfig(
                    payload=messaging.APNSPayload(
                        aps=messaging.Aps(
                            sound='default',
                            badge=1
                        )
                    )
                ),
                token=fcm_token
            )
            
            # Send message
            response = messaging.send(message)
            
            print(f'✅ Successfully sent notification: {response}')
            print(f'📱 Target token: {fcm_token}')
            print(f'📬 Title: {title}')
            print(f'📝 Body: {body}')
            print(f'📦 Data: {data}')
            
            return response
            
        except Exception as error:
            print(f'❌ Error sending notification: {error}')
            
            # Handle specific errors
            if 'invalid-registration-token' in str(error) or \
               'registration-token-not-registered' in str(error):
                print('🗑️ Invalid or unregistered FCM token')
            
            raise error
    
    def send_multicast_notification(
        self,
        fcm_tokens: List[str],
        title: str,
        body: str,
        data: Optional[Dict[str, str]] = None
    ) -> Dict:
        """
        Kirim notifikasi ke multiple devices
        
        Args:
            fcm_tokens: List of FCM tokens
            title: Judul notifikasi
            body: Isi notifikasi
            data: Data payload
            
        Returns:
            dict: Response dengan success dan failure counts
        """
        try:
            if not fcm_tokens or len(fcm_tokens) == 0:
                raise ValueError('fcm_tokens must be a non-empty list')
            
            if len(fcm_tokens) > 500:
                raise ValueError('Maximum 500 tokens per batch')
            
            # Prepare data
            if data is None:
                data = {}
            
            data_payload = {
                **{k: str(v) for k, v in data.items()},
                'timestamp': str(int(time.time() * 1000))
            }
            
            # Construct message
            message = messaging.MulticastMessage(
                notification=messaging.Notification(
                    title=title,
                    body=body
                ),
                data=data_payload,
                android=messaging.AndroidConfig(
                    priority='high',
                    notification=messaging.AndroidNotification(
                        sound='default',
                        color='#FF6B6B'
                    )
                ),
                tokens=fcm_tokens
            )
            
            # Send message
            response = messaging.send_multicast(message)
            
            print(f'✅ Successfully sent {response.success_count} notifications')
            print(f'❌ Failed to send {response.failure_count} notifications')
            
            # Log failed tokens
            if response.failure_count > 0:
                for idx, resp in enumerate(response.responses):
                    if not resp.success:
                        print(f'Failed token {idx}: {fcm_tokens[idx]} - {resp.exception}')
            
            return {
                'success_count': response.success_count,
                'failure_count': response.failure_count,
                'responses': response.responses
            }
            
        except Exception as error:
            print(f'❌ Error sending multicast notification: {error}')
            raise error
    
    def send_to_topic(
        self,
        topic: str,
        title: str,
        body: str,
        data: Optional[Dict[str, str]] = None
    ) -> str:
        """
        Kirim notifikasi ke topic
        
        Args:
            topic: Topic name
            title: Judul notifikasi
            body: Isi notifikasi
            data: Data payload
            
        Returns:
            str: Message ID
        """
        try:
            if data is None:
                data = {}
            
            data_payload = {
                **{k: str(v) for k, v in data.items()},
                'timestamp': str(int(time.time() * 1000))
            }
            
            message = messaging.Message(
                notification=messaging.Notification(
                    title=title,
                    body=body
                ),
                data=data_payload,
                topic=topic
            )
            
            response = messaging.send(message)
            
            print(f'✅ Successfully sent notification to topic: {topic}')
            print(f'📬 Message ID: {response}')
            
            return response
            
        except Exception as error:
            print(f'❌ Error sending to topic: {error}')
            raise error

# Create singleton instance
fcm_service = FCMService()
```

### 3. Flask API Example

#### File: `app.py`

```python
# app.py
from flask import Flask, request, jsonify
from fcm_service import fcm_service
import firebase_config  # Initialize Firebase

app = Flask(__name__)

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'OK',
        'message': 'FCM Backend is running'
    })

@app.route('/api/notifications/send', methods=['POST'])
def send_notification():
    """Send notification to single device"""
    try:
        data = request.get_json()
        
        fcm_token = data.get('fcmToken')
        title = data.get('title')
        body = data.get('body')
        payload = data.get('data', {})
        
        # Validation
        if not fcm_token or not title or not body:
            return jsonify({
                'success': False,
                'message': 'fcmToken, title, and body are required'
            }), 400
        
        message_id = fcm_service.send_targeted_notification(
            fcm_token,
            title,
            body,
            payload
        )
        
        return jsonify({
            'success': True,
            'message': 'Notification sent successfully',
            'messageId': message_id
        }), 200
        
    except Exception as error:
        print(f'Error in send_notification: {error}')
        return jsonify({
            'success': False,
            'message': 'Failed to send notification',
            'error': str(error)
        }), 500

@app.route('/api/notifications/send-multiple', methods=['POST'])
def send_multiple_notifications():
    """Send notification to multiple devices"""
    try:
        data = request.get_json()
        
        fcm_tokens = data.get('fcmTokens')
        title = data.get('title')
        body = data.get('body')
        payload = data.get('data', {})
        
        if not fcm_tokens or len(fcm_tokens) == 0:
            return jsonify({
                'success': False,
                'message': 'fcmTokens must be a non-empty array'
            }), 400
        
        result = fcm_service.send_multicast_notification(
            fcm_tokens,
            title,
            body,
            payload
        )
        
        return jsonify({
            'success': True,
            'message': 'Notifications sent',
            **result
        }), 200
        
    except Exception as error:
        print(f'Error in send_multiple_notifications: {error}')
        return jsonify({
            'success': False,
            'message': 'Failed to send notifications',
            'error': str(error)
        }), 500

if __name__ == '__main__':
    app.run(debug=True, port=3000)
```

---

*Dokumentasi dilanjutkan di bagian 2...*

**File ini akan dilanjutkan dengan:**
- PHP Implementation
- Use Case E-Commerce
- Best Practices
- Troubleshooting

---

*Next: Lihat FCM_BACKEND_GUIDE_PART2.md untuk implementasi lengkap*
