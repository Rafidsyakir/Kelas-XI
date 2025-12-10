# 🔥 Firebase Cloud Messaging - Backend Implementation Guide (Part 2)

Lanjutan dari FCM_BACKEND_GUIDE.md

---

## PHP Implementation

### 1. Setup

```bash
composer require kreait/firebase-php
```

### 2. Configuration

#### File: `config/firebase.php`

```php
<?php
// config/firebase.php

require_once __DIR__ . '/../vendor/autoload.php';

use Kreait\Firebase\Factory;

class FirebaseConfig {
    private static $messaging = null;
    
    public static function getMessaging() {
        if (self::$messaging === null) {
            $serviceAccountPath = getenv('FIREBASE_SERVICE_ACCOUNT_PATH') 
                ?: __DIR__ . '/../firebase-service-account.json';
            
            $factory = (new Factory)->withServiceAccount($serviceAccountPath);
            self::$messaging = $factory->createMessaging();
            
            echo "✅ Firebase Admin SDK initialized successfully\n";
        }
        
        return self::$messaging;
    }
}
```

### 3. FCM Service

#### File: `services/FCMService.php`

```php
<?php
// services/FCMService.php

require_once __DIR__ . '/../config/firebase.php';

use Kreait\Firebase\Messaging\CloudMessage;
use Kreait\Firebase\Messaging\Notification;
use Kreait\Firebase\Messaging\AndroidConfig;

class FCMService {
    private $messaging;
    
    public function __construct() {
        $this->messaging = FirebaseConfig::getMessaging();
    }
    
    /**
     * Kirim notifikasi ke satu device
     * 
     * @param string $fcmToken FCM Token dari device target
     * @param string $title Judul notifikasi
     * @param string $body Isi notifikasi
     * @param array $data Data payload
     * @return string Message ID
     */
    public function sendTargetedNotification($fcmToken, $title, $body, $data = []) {
        try {
            // Validasi
            if (empty($fcmToken) || empty($title) || empty($body)) {
                throw new Exception('fcmToken, title, and body are required');
            }
            
            // Convert data values ke string
            $dataPayload = array_map('strval', $data);
            $dataPayload['timestamp'] = (string)(time() * 1000);
            
            // Construct message
            $message = CloudMessage::withTarget('token', $fcmToken)
                ->withNotification(Notification::create($title, $body))
                ->withData($dataPayload)
                ->withAndroidConfig(
                    AndroidConfig::fromArray([
                        'priority' => 'high',
                        'notification' => [
                            'sound' => 'default',
                            'color' => '#FF6B6B',
                            'channel_id' => 'fcm_default_channel'
                        ]
                    ])
                );
            
            // Send message
            $response = $this->messaging->send($message);
            
            echo "✅ Successfully sent notification: $response\n";
            echo "📱 Target token: $fcmToken\n";
            echo "📬 Title: $title\n";
            echo "📝 Body: $body\n";
            
            return $response;
            
        } catch (Exception $error) {
            echo "❌ Error sending notification: " . $error->getMessage() . "\n";
            throw $error;
        }
    }
    
    /**
     * Kirim notifikasi ke multiple devices
     */
    public function sendMulticastNotification($fcmTokens, $title, $body, $data = []) {
        try {
            if (empty($fcmTokens) || !is_array($fcmTokens)) {
                throw new Exception('fcmTokens must be a non-empty array');
            }
            
            if (count($fcmTokens) > 500) {
                throw new Exception('Maximum 500 tokens per batch');
            }
            
            $dataPayload = array_map('strval', $data);
            $dataPayload['timestamp'] = (string)(time() * 1000);
            
            $message = CloudMessage::new()
                ->withNotification(Notification::create($title, $body))
                ->withData($dataPayload);
            
            $response = $this->messaging->sendMulticast($message, $fcmTokens);
            
            echo "✅ Successfully sent " . $response->successes()->count() . " notifications\n";
            echo "❌ Failed to send " . $response->failures()->count() . " notifications\n";
            
            return [
                'success_count' => $response->successes()->count(),
                'failure_count' => $response->failures()->count()
            ];
            
        } catch (Exception $error) {
            echo "❌ Error sending multicast notification: " . $error->getMessage() . "\n";
            throw $error;
        }
    }
    
    /**
     * Kirim notifikasi ke topic
     */
    public function sendToTopic($topic, $title, $body, $data = []) {
        try {
            $dataPayload = array_map('strval', $data);
            $dataPayload['timestamp'] = (string)(time() * 1000);
            
            $message = CloudMessage::withTarget('topic', $topic)
                ->withNotification(Notification::create($title, $body))
                ->withData($dataPayload);
            
            $response = $this->messaging->send($message);
            
            echo "✅ Successfully sent notification to topic: $topic\n";
            echo "📬 Message ID: $response\n";
            
            return $response;
            
        } catch (Exception $error) {
            echo "❌ Error sending to topic: " . $error->getMessage() . "\n";
            throw $error;
        }
    }
}
```

### 4. API Endpoint (Laravel Example)

#### File: `app/Http/Controllers/NotificationController.php`

```php
<?php
// app/Http/Controllers/NotificationController.php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Services\FCMService;

class NotificationController extends Controller {
    private $fcmService;
    
    public function __construct() {
        $this->fcmService = new FCMService();
    }
    
    /**
     * POST /api/notifications/send
     */
    public function sendNotification(Request $request) {
        try {
            $validated = $request->validate([
                'fcmToken' => 'required|string',
                'title' => 'required|string',
                'body' => 'required|string',
                'data' => 'nullable|array'
            ]);
            
            $messageId = $this->fcmService->sendTargetedNotification(
                $validated['fcmToken'],
                $validated['title'],
                $validated['body'],
                $validated['data'] ?? []
            );
            
            return response()->json([
                'success' => true,
                'message' => 'Notification sent successfully',
                'messageId' => $messageId
            ], 200);
            
        } catch (\Exception $error) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to send notification',
                'error' => $error->getMessage()
            ], 500);
        }
    }
}
```

---

## Use Case: E-Commerce

### Skenario: Pembeli menyelesaikan pembayaran, Seller menerima notifikasi

### 1. Database Structure

```sql
-- Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255),
    role ENUM('buyer', 'seller'),
    fcm_token TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    buyer_id INT,
    seller_id INT,
    product_name VARCHAR(255),
    amount DECIMAL(10, 2),
    status ENUM('pending', 'paid', 'processing', 'shipped', 'completed'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id)
);
```

### 2. Node.js Implementation

#### File: `services/order.service.js`

```javascript
// services/order.service.js
const fcmService = require('./fcm.service');
const db = require('../config/database'); // Assume MySQL connection

class OrderService {
  
  /**
   * Process payment dan kirim notifikasi ke seller
   */
  async processPayment(orderId, paymentData) {
    try {
      // 1. Update order status menjadi 'paid'
      await db.query(
        'UPDATE orders SET status = ? WHERE id = ?',
        ['paid', orderId]
      );
      
      // 2. Get order details dengan seller info
      const [orders] = await db.query(`
        SELECT 
          o.id,
          o.product_name,
          o.amount,
          b.name as buyer_name,
          s.id as seller_id,
          s.name as seller_name,
          s.fcm_token as seller_fcm_token
        FROM orders o
        JOIN users b ON o.buyer_id = b.id
        JOIN users s ON o.seller_id = s.id
        WHERE o.id = ?
      `, [orderId]);
      
      if (!orders || orders.length === 0) {
        throw new Error('Order not found');
      }
      
      const order = orders[0];
      
      // 3. Validasi seller memiliki FCM token
      if (!order.seller_fcm_token) {
        console.warn(`⚠️ Seller ${order.seller_name} doesn't have FCM token`);
        return {
          success: true,
          message: 'Payment processed but notification not sent',
          order_id: orderId
        };
      }
      
      // 4. Kirim notifikasi ke seller
      const notificationTitle = '💰 Pembayaran Diterima!';
      const notificationBody = `${order.buyer_name} telah membayar pesanan "${order.product_name}" sebesar Rp ${this.formatCurrency(order.amount)}`;
      
      const notificationData = {
        type: 'payment_received',
        order_id: String(order.id),
        buyer_name: order.buyer_name,
        product_name: order.product_name,
        amount: String(order.amount),
        action: 'open_order_details'
      };
      
      await fcmService.sendTargetedNotification(
        order.seller_fcm_token,
        notificationTitle,
        notificationBody,
        notificationData
      );
      
      console.log(`✅ Payment notification sent to seller: ${order.seller_name}`);
      
      // 5. Optional: Log notification history
      await this.logNotification(order.seller_id, orderId, 'payment_received');
      
      return {
        success: true,
        message: 'Payment processed and notification sent',
        order_id: orderId,
        seller_notified: true
      };
      
    } catch (error) {
      console.error('❌ Error processing payment:', error);
      throw error;
    }
  }
  
  /**
   * Kirim notifikasi update status ke buyer
   */
  async notifyOrderStatusUpdate(orderId, newStatus) {
    try {
      // Get order dengan buyer info
      const [orders] = await db.query(`
        SELECT 
          o.id,
          o.product_name,
          o.status,
          b.name as buyer_name,
          b.fcm_token as buyer_fcm_token,
          s.name as seller_name
        FROM orders o
        JOIN users b ON o.buyer_id = b.id
        JOIN users s ON o.seller_id = s.id
        WHERE o.id = ?
      `, [orderId]);
      
      if (!orders || orders.length === 0 || !orders[0].buyer_fcm_token) {
        throw new Error('Order or buyer FCM token not found');
      }
      
      const order = orders[0];
      
      // Status message mapping
      const statusMessages = {
        'processing': '⚙️ Pesanan sedang diproses',
        'shipped': '🚚 Pesanan telah dikirim',
        'completed': '✅ Pesanan selesai'
      };
      
      const title = statusMessages[newStatus] || '📦 Update Status Pesanan';
      const body = `Pesanan "${order.product_name}" dari ${order.seller_name} - Status: ${newStatus}`;
      
      await fcmService.sendTargetedNotification(
        order.buyer_fcm_token,
        title,
        body,
        {
          type: 'order_status_update',
          order_id: String(orderId),
          status: newStatus,
          product_name: order.product_name,
          action: 'open_order_tracking'
        }
      );
      
      console.log(`✅ Status update notification sent to buyer: ${order.buyer_name}`);
      
      return { success: true };
      
    } catch (error) {
      console.error('❌ Error sending status update notification:', error);
      throw error;
    }
  }
  
  /**
   * Kirim notifikasi ke multiple sellers (flash sale, promo, dll)
   */
  async notifyAllSellers(title, body, data = {}) {
    try {
      // Get all active seller FCM tokens
      const [sellers] = await db.query(`
        SELECT fcm_token
        FROM users
        WHERE role = 'seller' 
        AND fcm_token IS NOT NULL 
        AND fcm_token != ''
      `);
      
      if (!sellers || sellers.length === 0) {
        console.warn('⚠️ No sellers with FCM tokens found');
        return { success: false, message: 'No sellers to notify' };
      }
      
      const fcmTokens = sellers.map(s => s.fcm_token);
      
      // Send multicast notification
      const result = await fcmService.sendMulticastNotification(
        fcmTokens,
        title,
        body,
        {
          type: 'seller_broadcast',
          ...data
        }
      );
      
      console.log(`✅ Broadcast notification sent to ${result.successCount} sellers`);
      
      return {
        success: true,
        sellers_notified: result.successCount,
        failed: result.failureCount
      };
      
    } catch (error) {
      console.error('❌ Error sending broadcast to sellers:', error);
      throw error;
    }
  }
  
  /**
   * Log notification history untuk tracking
   */
  async logNotification(userId, orderId, notificationType) {
    try {
      await db.query(`
        INSERT INTO notification_logs 
        (user_id, order_id, type, sent_at)
        VALUES (?, ?, ?, NOW())
      `, [userId, orderId, notificationType]);
    } catch (error) {
      console.error('Error logging notification:', error);
    }
  }
  
  /**
   * Format currency
   */
  formatCurrency(amount) {
    return new Intl.NumberFormat('id-ID').format(amount);
  }
}

module.exports = new OrderService();
```

### 3. API Controller untuk Payment

#### File: `controllers/payment.controller.js`

```javascript
// controllers/payment.controller.js
const orderService = require('../services/order.service');

class PaymentController {
  
  /**
   * POST /api/payments/process
   * Process payment dan trigger notification
   */
  async processPayment(req, res) {
    try {
      const { orderId, paymentMethod, amount } = req.body;
      
      // Validation
      if (!orderId || !paymentMethod || !amount) {
        return res.status(400).json({
          success: false,
          message: 'orderId, paymentMethod, and amount are required'
        });
      }
      
      // Process payment (integrate with payment gateway)
      const paymentResult = await this.executePayment(paymentMethod, amount);
      
      if (!paymentResult.success) {
        return res.status(400).json({
          success: false,
          message: 'Payment failed',
          error: paymentResult.error
        });
      }
      
      // Update order dan kirim notifikasi
      const result = await orderService.processPayment(orderId, {
        payment_method: paymentMethod,
        transaction_id: paymentResult.transactionId
      });
      
      res.status(200).json({
        success: true,
        message: 'Payment processed successfully',
        ...result
      });
      
    } catch (error) {
      console.error('Error in processPayment:', error);
      res.status(500).json({
        success: false,
        message: 'Failed to process payment',
        error: error.message
      });
    }
  }
  
  /**
   * Simulate payment execution
   * Replace with actual payment gateway integration
   */
  async executePayment(paymentMethod, amount) {
    // Simulate payment gateway call
    return {
      success: true,
      transactionId: `TRX-${Date.now()}`,
      method: paymentMethod,
      amount: amount
    };
  }
}

module.exports = new PaymentController();
```

### 4. Python Implementation

#### File: `order_service.py`

```python
# order_service.py
from fcm_service import fcm_service
import mysql.connector
from datetime import datetime

class OrderService:
    def __init__(self, db_config):
        self.db_config = db_config
    
    def process_payment(self, order_id: int, payment_data: dict) -> dict:
        """Process payment dan kirim notifikasi ke seller"""
        try:
            conn = mysql.connector.connect(**self.db_config)
            cursor = conn.cursor(dictionary=True)
            
            # 1. Update order status
            cursor.execute(
                "UPDATE orders SET status = %s WHERE id = %s",
                ('paid', order_id)
            )
            conn.commit()
            
            # 2. Get order details
            cursor.execute("""
                SELECT 
                    o.id,
                    o.product_name,
                    o.amount,
                    b.name as buyer_name,
                    s.id as seller_id,
                    s.name as seller_name,
                    s.fcm_token as seller_fcm_token
                FROM orders o
                JOIN users b ON o.buyer_id = b.id
                JOIN users s ON o.seller_id = s.id
                WHERE o.id = %s
            """, (order_id,))
            
            order = cursor.fetchone()
            
            if not order:
                raise Exception('Order not found')
            
            # 3. Validate FCM token
            if not order['seller_fcm_token']:
                print(f"⚠️ Seller {order['seller_name']} doesn't have FCM token")
                return {
                    'success': True,
                    'message': 'Payment processed but notification not sent',
                    'order_id': order_id
                }
            
            # 4. Send notification to seller
            title = '💰 Pembayaran Diterima!'
            body = f"{order['buyer_name']} telah membayar pesanan \"{order['product_name']}\" sebesar Rp {self.format_currency(order['amount'])}"
            
            data = {
                'type': 'payment_received',
                'order_id': str(order['id']),
                'buyer_name': order['buyer_name'],
                'product_name': order['product_name'],
                'amount': str(order['amount']),
                'action': 'open_order_details'
            }
            
            fcm_service.send_targeted_notification(
                order['seller_fcm_token'],
                title,
                body,
                data
            )
            
            print(f"✅ Payment notification sent to seller: {order['seller_name']}")
            
            cursor.close()
            conn.close()
            
            return {
                'success': True,
                'message': 'Payment processed and notification sent',
                'order_id': order_id,
                'seller_notified': True
            }
            
        except Exception as error:
            print(f'❌ Error processing payment: {error}')
            raise error
    
    def notify_order_status_update(self, order_id: int, new_status: str) -> dict:
        """Kirim notifikasi update status ke buyer"""
        try:
            conn = mysql.connector.connect(**self.db_config)
            cursor = conn.cursor(dictionary=True)
            
            cursor.execute("""
                SELECT 
                    o.id,
                    o.product_name,
                    b.name as buyer_name,
                    b.fcm_token as buyer_fcm_token,
                    s.name as seller_name
                FROM orders o
                JOIN users b ON o.buyer_id = b.id
                JOIN users s ON o.seller_id = s.id
                WHERE o.id = %s
            """, (order_id,))
            
            order = cursor.fetchone()
            
            if not order or not order['buyer_fcm_token']:
                raise Exception('Order or buyer FCM token not found')
            
            # Status messages
            status_messages = {
                'processing': '⚙️ Pesanan sedang diproses',
                'shipped': '🚚 Pesanan telah dikirim',
                'completed': '✅ Pesanan selesai'
            }
            
            title = status_messages.get(new_status, '📦 Update Status Pesanan')
            body = f"Pesanan \"{order['product_name']}\" dari {order['seller_name']} - Status: {new_status}"
            
            fcm_service.send_targeted_notification(
                order['buyer_fcm_token'],
                title,
                body,
                {
                    'type': 'order_status_update',
                    'order_id': str(order_id),
                    'status': new_status,
                    'product_name': order['product_name'],
                    'action': 'open_order_tracking'
                }
            )
            
            print(f"✅ Status update notification sent to buyer: {order['buyer_name']}")
            
            cursor.close()
            conn.close()
            
            return {'success': True}
            
        except Exception as error:
            print(f'❌ Error sending status update: {error}')
            raise error
    
    @staticmethod
    def format_currency(amount: float) -> str:
        """Format currency to Indonesian Rupiah"""
        return f"{amount:,.0f}".replace(',', '.')

# Usage
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': 'password',
    'database': 'ecommerce'
}

order_service = OrderService(db_config)
```

### 5. Complete Flow Example

```javascript
// Complete payment flow
const express = require('express');
const router = express.Router();
const orderService = require('./services/order.service');

// Buyer completes payment
router.post('/checkout/complete', async (req, res) => {
  try {
    const { orderId, paymentMethod, buyerId } = req.body;
    
    // 1. Validate payment dengan payment gateway
    const paymentValid = await validatePayment(orderId, paymentMethod);
    
    if (!paymentValid) {
      return res.status(400).json({ 
        success: false, 
        message: 'Payment validation failed' 
      });
    }
    
    // 2. Process payment dan kirim notifikasi ke seller
    const result = await orderService.processPayment(orderId, {
      payment_method: paymentMethod,
      buyer_id: buyerId,
      paid_at: new Date()
    });
    
    // 3. Return success response
    res.status(200).json({
      success: true,
      message: 'Payment completed successfully',
      order_id: orderId,
      seller_notified: result.seller_notified
    });
    
  } catch (error) {
    console.error('Checkout error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Checkout failed',
      error: error.message 
    });
  }
});

// Seller updates order status
router.post('/orders/:orderId/status', async (req, res) => {
  try {
    const { orderId } = req.params;
    const { status } = req.body;
    
    // Update status di database
    await updateOrderStatus(orderId, status);
    
    // Kirim notifikasi ke buyer
    await orderService.notifyOrderStatusUpdate(orderId, status);
    
    res.status(200).json({
      success: true,
      message: 'Status updated and buyer notified'
    });
    
  } catch (error) {
    console.error('Status update error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to update status',
      error: error.message 
    });
  }
});
```

---

## Best Practices

### 1. Security

✅ **DO:**
- Store Service Account Key securely
- Use environment variables untuk sensitive data
- Never commit credentials ke Git
- Implement authentication di API endpoints
- Validate FCM tokens sebelum menyimpan
- Rate limit notification sending

❌ **DON'T:**
- Hardcode service account key di code
- Expose API endpoints tanpa authentication
- Send sensitive data melalui notification payload
- Store credentials di frontend

### 2. Token Management

```javascript
// Token validation dan cleanup
class TokenManager {
  async validateAndSaveToken(userId, fcmToken) {
    try {
      // Validate format
      if (!fcmToken || fcmToken.length < 100) {
        throw new Error('Invalid FCM token format');
      }
      
      // Check if token already exists for another user
      const existingUser = await db.query(
        'SELECT id FROM users WHERE fcm_token = ? AND id != ?',
        [fcmToken, userId]
      );
      
      if (existingUser.length > 0) {
        // Token sudah digunakan user lain, clear old token
        await db.query(
          'UPDATE users SET fcm_token = NULL WHERE fcm_token = ?',
          [fcmToken]
        );
      }
      
      // Save new token
      await db.query(
        'UPDATE users SET fcm_token = ?, token_updated_at = NOW() WHERE id = ?',
        [fcmToken, userId]
      );
      
      console.log(`✅ FCM token saved for user ${userId}`);
      
    } catch (error) {
      console.error('Error saving FCM token:', error);
      throw error;
    }
  }
  
  async removeInvalidTokens() {
    // Cleanup tokens yang gagal send
    // Run this periodically (cron job)
    const invalidTokens = await db.query(`
      SELECT fcm_token 
      FROM failed_notifications 
      WHERE error_code IN ('invalid-registration-token', 'registration-token-not-registered')
      GROUP BY fcm_token
    `);
    
    for (const token of invalidTokens) {
      await db.query(
        'UPDATE users SET fcm_token = NULL WHERE fcm_token = ?',
        [token.fcm_token]
      );
    }
  }
}
```

### 3. Error Handling

```javascript
async function sendNotificationWithRetry(fcmToken, title, body, data, maxRetries = 3) {
  let lastError;
  
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      const result = await fcmService.sendTargetedNotification(
        fcmToken,
        title,
        body,
        data
      );
      
      return { success: true, result };
      
    } catch (error) {
      lastError = error;
      
      // Don't retry for permanent errors
      if (error.code === 'messaging/invalid-registration-token' ||
          error.code === 'messaging/registration-token-not-registered') {
        console.error('Permanent error, not retrying:', error.code);
        break;
      }
      
      // Exponential backoff
      if (attempt < maxRetries) {
        const delay = Math.pow(2, attempt) * 1000;
        console.log(`Retry attempt ${attempt} after ${delay}ms`);
        await new Promise(resolve => setTimeout(resolve, delay));
      }
    }
  }
  
  return { success: false, error: lastError };
}
```

### 4. Notification Scheduling

```javascript
// Using node-cron for scheduled notifications
const cron = require('node-cron');
const orderService = require('./services/order.service');

// Send daily summary ke sellers jam 9 pagi
cron.schedule('0 9 * * *', async () => {
  console.log('📊 Sending daily summary to sellers');
  
  try {
    const sellers = await db.query(`
      SELECT id, name, fcm_token 
      FROM users 
      WHERE role = 'seller' AND fcm_token IS NOT NULL
    `);
    
    for (const seller of sellers) {
      // Get statistics
      const stats = await getSellerDailyStats(seller.id);
      
      await fcmService.sendTargetedNotification(
        seller.fcm_token,
        '📊 Ringkasan Harian',
        `Hari ini: ${stats.orders_count} pesanan baru, Pendapatan: Rp ${stats.revenue}`,
        {
          type: 'daily_summary',
          orders_count: String(stats.orders_count),
          revenue: String(stats.revenue)
        }
      );
    }
    
    console.log('✅ Daily summary sent to all sellers');
    
  } catch (error) {
    console.error('❌ Error sending daily summary:', error);
  }
});
```

### 5. Performance Optimization

```javascript
// Batch processing untuk banyak notifikasi
async function sendBulkNotifications(notifications) {
  const BATCH_SIZE = 500;
  const results = [];
  
  // Group by FCM tokens
  for (let i = 0; i < notifications.length; i += BATCH_SIZE) {
    const batch = notifications.slice(i, i + BATCH_SIZE);
    
    const tokens = batch.map(n => n.fcmToken);
    const title = batch[0].title; // Assume same title for batch
    const body = batch[0].body;
    
    try {
      const result = await fcmService.sendMulticastNotification(
        tokens,
        title,
        body,
        batch[0].data
      );
      
      results.push(result);
      
    } catch (error) {
      console.error(`Batch ${i / BATCH_SIZE + 1} failed:`, error);
    }
    
    // Rate limiting: delay between batches
    if (i + BATCH_SIZE < notifications.length) {
      await new Promise(resolve => setTimeout(resolve, 1000));
    }
  }
  
  return results;
}
```

---

## Troubleshooting

### Common Issues

#### 1. **Error: "messaging/invalid-registration-token"**

**Penyebab:**
- FCM token tidak valid
- Token sudah expired
- Token dari project Firebase yang berbeda

**Solusi:**
```javascript
// Remove invalid token from database
await db.query(
  'UPDATE users SET fcm_token = NULL WHERE fcm_token = ?',
  [invalidToken]
);

// Ask user to re-login untuk generate token baru
```

#### 2. **Error: "messaging/registration-token-not-registered"**

**Penyebab:**
- App sudah di-uninstall
- User sudah logout
- Token expired

**Solusi:**
```javascript
// Same as above - remove token
// Implement token refresh mechanism
```

#### 3. **Notification tidak muncul di device**

**Checklist:**
- ✅ App dalam foreground? (Need custom handling)
- ✅ Notification permission granted?
- ✅ NotificationChannel created? (Android 8.0+)
- ✅ Battery optimization disabled?
- ✅ Google Play Services installed?

**Debug:**
```kotlin
// Add logs di MyFirebaseMessagingService
override fun onMessageReceived(remoteMessage: RemoteMessage) {
    Log.d("FCM", "Message received from: ${remoteMessage.from}")
    Log.d("FCM", "Notification: ${remoteMessage.notification}")
    Log.d("FCM", "Data: ${remoteMessage.data}")
    
    // Force show notification
    sendNotification(
        remoteMessage.notification?.title ?: "Test",
        remoteMessage.notification?.body ?: "Test body"
    )
}
```

#### 4. **Error: "messaging/server-unavailable"**

**Penyebab:**
- FCM server temporarily down
- Network issues

**Solusi:**
```javascript
// Implement retry logic with exponential backoff
async function sendWithRetry(fcmToken, title, body, maxRetries = 3) {
  for (let i = 0; i < maxRetries; i++) {
    try {
      return await fcmService.sendTargetedNotification(fcmToken, title, body);
    } catch (error) {
      if (i === maxRetries - 1) throw error;
      await new Promise(resolve => setTimeout(resolve, Math.pow(2, i) * 1000));
    }
  }
}
```

#### 5. **Data payload tidak sampai ke app**

**Penyebab:**
- Data values bukan string
- Key name tidak sesuai

**Solusi:**
```javascript
// Convert semua values ke string
const data = {
  order_id: String(orderId),        // ✅ Correct
  amount: String(amount),            // ✅ Correct
  timestamp: String(Date.now())      // ✅ Correct
  // order_id: orderId,              // ❌ Wrong (number)
  // amount: amount,                 // ❌ Wrong (number)
};
```

### Testing Tips

#### 1. **Test dengan Firebase Console**

1. Go to Firebase Console → Cloud Messaging
2. Click "Send test message"
3. Paste FCM token
4. Send notification

#### 2. **Test dengan Postman**

```json
POST http://localhost:3000/api/notifications/send

{
  "fcmToken": "YOUR_FCM_TOKEN_HERE",
  "title": "Test Notification",
  "body": "This is a test message",
  "data": {
    "type": "test",
    "timestamp": "1234567890"
  }
}
```

#### 3. **Test dengan cURL**

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

## 📚 Additional Resources

- [Firebase Admin SDK Documentation](https://firebase.google.com/docs/admin/setup)
- [FCM Server Reference](https://firebase.google.com/docs/cloud-messaging/server)
- [FCM Best Practices](https://firebase.google.com/docs/cloud-messaging/best-practices)
- [Error Codes Reference](https://firebase.google.com/docs/cloud-messaging/http-server-ref#error-codes)

---

## ✅ Checklist Implementasi

### Backend Setup
- [ ] Install Firebase Admin SDK
- [ ] Download Service Account Key
- [ ] Setup environment variables
- [ ] Initialize Firebase Admin
- [ ] Create FCM service functions

### API Development
- [ ] Create notification endpoints
- [ ] Implement authentication
- [ ] Add input validation
- [ ] Setup error handling
- [ ] Add logging

### Database
- [ ] Add fcm_token column ke users table
- [ ] Create notification_logs table
- [ ] Setup indexes
- [ ] Implement token cleanup

### Testing
- [ ] Test single notification
- [ ] Test multicast notification
- [ ] Test topic notification
- [ ] Test error scenarios
- [ ] Load testing

### Production
- [ ] Setup environment variables
- [ ] Configure HTTPS
- [ ] Setup monitoring/logging
- [ ] Implement rate limiting
- [ ] Setup backup & recovery

---

**🎉 Selesai!** Backend FCM sudah siap untuk production.

Untuk pertanyaan lebih lanjut, lihat:
- [FCM_ANDROID_GUIDE.md](./FCM_ANDROID_GUIDE.md) - Android implementation
- Firebase Documentation - Official docs
