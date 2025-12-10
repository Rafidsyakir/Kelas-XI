# 🔥 Firebase Cloud Messaging (FCM) - Complete Implementation Guide

Panduan lengkap implementasi Firebase Cloud Messaging untuk aplikasi Android dengan backend support.

---

## 📁 Struktur File

```
Firebase Tutorial/
├── 📱 Android Implementation
│   ├── FCM_ANDROID_GUIDE.md                 # Dokumentasi lengkap Android
│   ├── MyFirebaseMessagingService.kt        # FCM Service untuk receive notifikasi
│   ├── MainActivityFCM.kt                   # Activity dengan permission handling
│   └── activity_main_fcm.xml                # UI layout untuk testing FCM
│
├── 🖥️ Backend Implementation
│   ├── FCM_BACKEND_GUIDE.md                 # Dokumentasi backend (Part 1)
│   ├── FCM_BACKEND_GUIDE_PART2.md           # Use cases & best practices (Part 2)
│   │
│   ├── backend-nodejs/
│   │   ├── fcm_service.js                   # FCM Service (Node.js)
│   │   └── order_service_ecommerce.js       # E-commerce use case example
│   │
│   └── backend-python/
│       └── fcm_service.py                   # FCM Service (Python)
│
└── README_FCM.md (this file)                # Quick start guide
```

---

## 🚀 Quick Start

### 1️⃣ Android Implementation

#### **Step 1: Setup Firebase**
1. Tambahkan `google-services.json` ke folder `app/`
2. Update `build.gradle.kts` dengan dependencies FCM

```kotlin
// app/build.gradle.kts
dependencies {
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

#### **Step 2: Update AndroidManifest.xml**

```xml
<manifest>
    <!-- Permission untuk notifikasi (Android 13+) -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
    <uses-permission android:name="android.permission.INTERNET"/>

    <application>
        <!-- FCM Service -->
        <service
            android:name=".MyFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT"/>
            </intent-filter>
        </service>

        <!-- Default notification channel -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="fcm_default_channel"/>
    </application>
</manifest>
```

#### **Step 3: Copy Files**
1. Copy `MyFirebaseMessagingService.kt` → `app/src/main/java/com/example/firebasetutorial/`
2. Copy `MainActivityFCM.kt` → `app/src/main/java/com/example/firebasetutorial/`
3. Copy `activity_main_fcm.xml` → `app/src/main/res/layout/`

#### **Step 4: Test**
1. Run aplikasi di device fisik (emulator tidak selalu reliable)
2. Allow notification permission
3. Copy FCM Token yang ditampilkan
4. Test dengan Firebase Console atau backend

📖 **Dokumentasi lengkap**: Baca [FCM_ANDROID_GUIDE.md](./FCM_ANDROID_GUIDE.md)

---

### 2️⃣ Backend Implementation (Node.js)

#### **Step 1: Install Dependencies**

```bash
npm install firebase-admin express
```

#### **Step 2: Download Service Account Key**

1. Go to **Firebase Console** → Project Settings → Service Accounts
2. Click **"Generate new private key"**
3. Save sebagai `firebase-service-account.json`
4. **JANGAN** commit file ini ke Git!

#### **Step 3: Setup Environment Variable**

```bash
# .env file
FIREBASE_SERVICE_ACCOUNT_PATH=./firebase-service-account.json
PORT=3000
```

#### **Step 4: Initialize Firebase Admin SDK**

```javascript
// config/firebase.js
const admin = require('firebase-admin');
const serviceAccount = require('../firebase-service-account.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

console.log('✅ Firebase Admin SDK initialized');

module.exports = admin;
```

#### **Step 5: Copy FCM Service**

Copy `backend-nodejs/fcm_service.js` ke project Anda.

#### **Step 6: Send Notification**

```javascript
// Example usage
const fcmService = require('./fcm_service');

async function sendWelcomeNotification(userFcmToken) {
  await fcmService.sendTargetedNotification(
    userFcmToken,
    'Welcome! 👋',
    'Thank you for joining us!',
    {
      type: 'welcome',
      user_id: '12345'
    }
  );
}
```

#### **Step 7: Run Server**

```bash
node app.js
# Server running on port 3000
```

📖 **Dokumentasi lengkap**: Baca [FCM_BACKEND_GUIDE.md](./FCM_BACKEND_GUIDE.md)

---

### 3️⃣ Backend Implementation (Python)

#### **Step 1: Install Dependencies**

```bash
pip install firebase-admin flask python-dotenv
```

#### **Step 2: Initialize Firebase Admin SDK**

```python
# firebase_config.py
import firebase_admin
from firebase_admin import credentials

cred = credentials.Certificate('firebase-service-account.json')
firebase_admin.initialize_app(cred)

print('✅ Firebase Admin SDK initialized')
```

#### **Step 3: Copy FCM Service**

Copy `backend-python/fcm_service.py` ke project Anda.

#### **Step 4: Send Notification**

```python
# Example usage
from fcm_service import fcm_service

def send_welcome_notification(user_fcm_token: str):
    fcm_service.send_targeted_notification(
        user_fcm_token,
        'Welcome! 👋',
        'Thank you for joining us!',
        {'type': 'welcome', 'user_id': '12345'}
    )
```

---

## 📚 Use Case: E-Commerce

### Skenario: Pembeli bayar pesanan → Seller dapat notifikasi

**Backend (Node.js):**

```javascript
const orderService = require('./order_service_ecommerce');

// Saat pembeli selesai bayar
async function handlePaymentSuccess(orderId, paymentData) {
  const result = await orderService.processPayment(orderId, paymentData);
  console.log(result);
  // ✅ Seller mendapat notifikasi: "John Doe telah membayar pesanan..."
}

// Saat seller update status
async function handleStatusUpdate(orderId, sellerId, newStatus) {
  const result = await orderService.updateOrderStatusAndNotifyBuyer(
    orderId,
    newStatus,
    sellerId
  );
  console.log(result);
  // ✅ Buyer mendapat notifikasi: "Pesanan telah dikirim..."
}
```

📖 **Contoh lengkap**: Lihat `backend-nodejs/order_service_ecommerce.js`

---

## 🧪 Testing

### 1. Test dengan Firebase Console

1. Go to **Firebase Console** → Cloud Messaging
2. Click **"Send test message"**
3. Paste FCM Token dari Android app
4. Send notification

### 2. Test dengan Postman

```http
POST http://localhost:3000/api/notifications/send
Content-Type: application/json

{
  "fcmToken": "YOUR_FCM_TOKEN_HERE",
  "title": "Test Notification",
  "body": "Hello from Postman!",
  "data": {
    "type": "test",
    "order_id": "12345"
  }
}
```

### 3. Test dengan cURL

```bash
curl -X POST http://localhost:3000/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "fcmToken": "YOUR_FCM_TOKEN",
    "title": "Test",
    "body": "Hello from cURL",
    "data": {"type": "test"}
  }'
```

---

## ⚠️ Common Issues & Solutions

### 1. **Notification tidak muncul di Android**

**Checklist:**
- ✅ Notification permission sudah granted?
- ✅ NotificationChannel sudah dibuat? (Android 8.0+)
- ✅ App dalam background? (Foreground perlu custom handling)
- ✅ FCM Token valid dan up-to-date?

**Solution:**
```kotlin
// Force create notification channel
private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "fcm_default_channel",
            "FCM Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
```

### 2. **Error: "messaging/invalid-registration-token"**

**Penyebab:** FCM token tidak valid atau expired

**Solution:**
```javascript
// Remove invalid token from database
await db.query(
  'UPDATE users SET fcm_token = NULL WHERE fcm_token = ?',
  [invalidToken]
);
```

### 3. **Backend error: "Service account key not found"**

**Penyebab:** Path ke service account key salah

**Solution:**
```javascript
// Use absolute path
const serviceAccountPath = path.join(__dirname, 'firebase-service-account.json');
const serviceAccount = require(serviceAccountPath);
```

### 4. **Data payload tidak sampai ke Android**

**Penyebab:** Data values bukan string

**Solution:**
```javascript
// ❌ Wrong
const data = { order_id: 12345 };

// ✅ Correct
const data = { order_id: String(12345) };
```

---

## 📊 Architecture Overview

```
┌─────────────────┐
│  Android App    │
│  (FCM Client)   │
└────────┬────────┘
         │
         │ 1. Get FCM Token
         │ 2. Send token to backend
         │
         ▼
┌─────────────────┐
│  Your Backend   │
│  (Node.js/      │
│   Python/PHP)   │
└────────┬────────┘
         │
         │ 3. Store FCM token in DB
         │ 4. Use Firebase Admin SDK
         │    to send notification
         │
         ▼
┌─────────────────┐
│  Firebase FCM   │
│  (Google)       │
└────────┬────────┘
         │
         │ 5. Deliver notification
         │
         ▼
┌─────────────────┐
│  Android App    │
│  (Receives      │
│   notification) │
└─────────────────┘
```

---

## 🔐 Security Best Practices

### ✅ DO:
- Store Service Account Key di environment variables
- Implement authentication di API endpoints
- Validate FCM tokens sebelum menyimpan
- Rate limit notification sending
- Log notification activities

### ❌ DON'T:
- Hardcode service account key di code
- Commit credentials ke Git
- Expose notification API tanpa auth
- Send sensitive data via notification payload
- Store FCM tokens tanpa encryption (production)

---

## 📖 Additional Documentation

- **Android**: [FCM_ANDROID_GUIDE.md](./FCM_ANDROID_GUIDE.md)
- **Backend**: [FCM_BACKEND_GUIDE.md](./FCM_BACKEND_GUIDE.md)
- **Use Cases**: [FCM_BACKEND_GUIDE_PART2.md](./FCM_BACKEND_GUIDE_PART2.md)
- **Official**: [Firebase Documentation](https://firebase.google.com/docs/cloud-messaging)

---

## ✅ Checklist Implementasi

### Android
- [ ] Add Firebase ke project
- [ ] Install dependencies FCM
- [ ] Copy `MyFirebaseMessagingService.kt`
- [ ] Copy `MainActivityFCM.kt` dan `activity_main_fcm.xml`
- [ ] Update `AndroidManifest.xml`
- [ ] Create NotificationChannel
- [ ] Request notification permission
- [ ] Test receive notification

### Backend
- [ ] Install Firebase Admin SDK
- [ ] Download Service Account Key
- [ ] Setup environment variables
- [ ] Initialize Firebase Admin
- [ ] Copy FCM service files
- [ ] Create API endpoints
- [ ] Add database untuk store FCM tokens
- [ ] Test send notification

### Database
- [ ] Add `fcm_token` column ke users table
- [ ] Create `notification_logs` table
- [ ] Setup indexes
- [ ] Implement token cleanup logic

### Production
- [ ] Setup HTTPS
- [ ] Configure rate limiting
- [ ] Setup monitoring/logging
- [ ] Implement error handling
- [ ] Setup backup & recovery
- [ ] Security audit

---

## 🎉 Ready to Go!

Anda sekarang memiliki:
- ✅ Android app yang bisa **menerima** FCM notifications
- ✅ Backend yang bisa **mengirim** FCM notifications
- ✅ E-commerce use case example
- ✅ Complete documentation

**Next Steps:**
1. Test end-to-end flow
2. Implement di real project
3. Customize sesuai kebutuhan
4. Deploy to production

---

## 💬 Support

Jika ada pertanyaan atau issues:
1. Baca dokumentasi lengkap terlebih dahulu
2. Check common issues section
3. Review example code

**Happy coding! 🚀**
